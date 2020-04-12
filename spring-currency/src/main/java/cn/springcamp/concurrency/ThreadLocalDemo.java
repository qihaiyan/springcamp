package cn.springcamp.concurrency;

class ThreadLocalDemo {
    private final ThreadLocal<Transaction> currentTransaction = ThreadLocal.withInitial(NullTransaction::new);

    Transaction currentTransaction() {
        Transaction current = currentTransaction.get();
        if (current.isNull()) {
            current = new TransactionImpl();
            currentTransaction.set(current);
        }
        return current;
    }
}

interface Transaction {
    boolean isNull();
}

class NullTransaction implements Transaction {
    public boolean isNull() {
        return true;
    }
}

class TransactionImpl implements Transaction {
    public boolean isNull() {
        return false;
    }
}