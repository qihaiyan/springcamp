package cn.springcamp.utils.alisms;

import cn.springcamp.utils.json.JsonUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
public class Sms {
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    private static final String SMS_SUCCESS_STATUS = "OK";
    private static final String SMS_SEND_URL = "https://ecs.aliyuncs.com/";
    private final Map<String, SmsConfig.Credential> iAcsClientMap = new ConcurrentHashMap<>();

    @Autowired
    private RestClient restClient;

    public void sendSms(SmsTemplate smsTemplate, SmsRequestParams smsRequestParams, String providerAccountJson) {
        String senderName = StringUtils.hasText(smsRequestParams.getSenderName())
                ? smsRequestParams.getSenderName()
                : smsTemplate.getSenderName();
        SmsConfig.Credential credential = getAccount(providerAccountJson, smsTemplate.getAccountCode());
        if (credential == null) {
            throw new RuntimeException("could not find one valid credential by accountCode: " + smsTemplate.getAccountCode());
        }
        sendSms(smsRequestParams.getPhoneNumbers(), smsRequestParams.getTemplateParam(), senderName, credential);
    }

    private void sendSms(String phoneNumber, String templateParam, String senderName, SmsConfig.Credential credential) {
        String timestamp = ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        Map<String, String> map = new TreeMap<>();
        map.put("Action", "SendSms");
        map.put("Version", "2017-05-25");
        map.put("Format", "JSON");
        map.put("AccessKeyId", credential.getKey());
        map.put("SignatureNonce", UUID.randomUUID().toString());
        map.put("Timestamp", timestamp);
        map.put("SignatureMethod", "HMAC-SHA1");
        map.put("SignatureVersion", "1.0");
        map.put("PhoneNumbers", phoneNumber);
        map.put("SignName", senderName);
        map.put("TemplateCode", "ali");
        map.put("TemplateParam", templateParam);

        String signature = sign(credential.getSecret(), map);

        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        map.forEach(multiValueMap::add);
        multiValueMap.add("Signature", signature);
        SmsResponseDto response = restClient.post()
                .uri(SMS_SEND_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(multiValueMap)
                .retrieve()
                .body(SmsResponseDto.class);
        log.info("Send SMS by Ali, phoneNumber: {}, templateCode: {}, templateParam: {}, senderName: {}, response: {}"
                , phoneNumber, "aliTemplateCode", templateParam, senderName, response);
        if (response != null && SMS_SUCCESS_STATUS.equals(response.getCode())) {
            return;
        }
        throw new RuntimeException("invalid account");
    }

    private SmsConfig.Credential getAccount(String providerAccountJson, String accountCode) {
        if (iAcsClientMap.containsKey(accountCode)) {
            return iAcsClientMap.get(accountCode);
        }

        SmsConfig smsConfig = JsonUtils.readValue(providerAccountJson, SmsConfig.class);
        if (CollectionUtils.isEmpty(smsConfig.getCredentials())) {
            return null;
        }
        smsConfig.getCredentials().forEach(
                credential -> iAcsClientMap.put(accountCode, new SmsConfig.Credential(credential.getKey(), credential.getSecret()))

        );
        return iAcsClientMap.get(accountCode);
    }

    private String sign(String secret, Map<String, String> input) {

        String canonicalizedQueryString = input.entrySet().stream()
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
        String stringToSign = "GET&%2F&" + URLEncoder.encode(canonicalizedQueryString, StandardCharsets.UTF_8);

        try {
            SecretKeySpec signKey = new SecretKeySpec((secret + "&").getBytes(), HMAC_SHA1_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(signKey);
            byte[] digest = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(digest);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    @Data
    static class SmsResponseDto {
        @JsonProperty("Code")
        private String code;
        @JsonProperty("Message")
        private String message;
        @JsonProperty("BizId")
        private String bizId;
        @JsonProperty("RequestId")
        private String requestId;
    }

    @Data
    static class SmsConfig {
        List<Credential> credentials = new ArrayList<>();

        @Data
        @AllArgsConstructor
        public static class Credential {
            private String key;
            private String secret;
        }
    }

    @Data
    @ToString
    public static class SmsTemplate {
        private String code;
        private String name;
        private String msgType;
        private String templateContent;
        private String senderName;
        private String accountCode;
        private LocalDateTime latestUpdateTime;
    }

    @Data
    @ToString
    public static class SmsRequestParams {
        private String templateCode;
        private String templateParam;
        private String phoneNumbers;
        private String productCode;
        private String exno = "";
        private String senderName = "";
        private String messageId;
    }
}
