package cn.springcamp.spring.rest.client;

import java.util.*;
import java.util.stream.Collectors;

public class DeepDiff {
    public <T> T sortNestedStructure(T obj) {
        if (obj instanceof Map<?, ?> map) {
            LinkedHashMap<Object, Object> sorted = map.entrySet().stream()
                    .filter(e -> e.getValue() != null)
                    .sorted(Comparator.comparing(e -> e.getKey().toString()))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> (Object) sortNestedStructure(entry.getValue()),
                            (oldVal, newVal) -> oldVal,
                            LinkedHashMap::new
                    ));
            return (T) sorted;
        } else if (obj instanceof List<?> list) {
            List<Object> sortedList = list.stream()
                    .map(this::sortNestedStructure)
                    .collect(Collectors.toList());

            if (areElementsHomogeneous(sortedList)) {
                sortedList.sort(Comparator.comparing(s -> {
                    if (s instanceof Map<?, ?> m) {
                        if (m.containsKey("name")) {
                            Object nm = m.get("name");
                            return nm == null ? "" : nm.toString();
                        }
                        return "";
                    }
                    return s == null ? "" : s.toString();
                }));
            }
            return (T) sortedList;
        } else {
            return obj;
        }
    }

    private boolean areElementsHomogeneous(List<?> list) {
        if (list.isEmpty()) return false;
        Class<?> elementType = list.getFirst().getClass();
        for (Object elem : list) {
            if (!elementType.equals(elem.getClass())) {
                return false;
            }
        }
        return true;
    }

    public List<String> diff(Object left, Object right) {
        List<String> diffs = new ArrayList<>();
        diffInternal(left, right, "", diffs);
        return diffs;
    }

    @SuppressWarnings("unchecked")
    private void diffInternal(Object l, Object r, String path, List<String> diffs) {
        if (l == null && r == null) return;
        if (l == null) {
            diffs.add(path + "  ← missing in left, right=" + toShort(r));
            return;
        }
        if (r == null) {
            diffs.add(path + "  ← missing in right, left=" + toShort(l));
            return;
        }

        if (l instanceof Map && r instanceof Map) {
            Map<Object, Object> lm = (Map<Object, Object>) l;
            Map<Object, Object> rm = (Map<Object, Object>) r;
            // 所有 key 的并集
            Set<Object> keys = new TreeSet<>(Comparator.comparing(Object::toString));
            keys.addAll(lm.keySet());
            keys.addAll(rm.keySet());
            for (Object key : keys) {
                String p = path + "/" + key.toString();
                diffInternal(lm.get(key), rm.get(key), p, diffs);
            }
            return;
        }

        if (l instanceof List && r instanceof List) {
            List<Object> ll = (List<Object>) l;
            List<Object> rl = (List<Object>) r;
            int min = Math.min(ll.size(), rl.size());
            for (int i = 0; i < min; i++) {
                diffInternal(ll.get(i), rl.get(i), path + "[" + i + "]", diffs);
            }
            if (ll.size() > rl.size()) {
                for (int i = min; i < ll.size(); i++) {
                    diffs.add(path + "[" + i + "]  ← extra in left = " + toShort(ll.get(i)));
                }
            } else if (rl.size() > ll.size()) {
                for (int i = min; i < rl.size(); i++) {
                    diffs.add(path + "[" + i + "]  ← extra in right = " + toShort(rl.get(i)));
                }
            }
            return;
        }

        // 类型不一致
        if (!l.getClass().equals(r.getClass())) {
            diffs.add(path + "  ← type differs, left=" + l.getClass().getSimpleName()
                    + ", right=" + r.getClass().getSimpleName()
                    + "  values: left=" + toShort(l) + ", right=" + toShort(r));
            return;
        }

        // 基本值比较
        if (!Objects.equals(l, r)) {
            diffs.add(path + "  ← value differs: left=" + toShort(l) + ", right=" + toShort(r));
        }
    }

    private static String toShort(Object o) {
        if (o == null) return "null";
        String s = o.toString();
        if (s.length() > 200) return s.substring(0, 200) + "...";
        return s;
    }
}
