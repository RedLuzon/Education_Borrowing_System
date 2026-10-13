package EducationBorrowingSystem;

public abstract class FinePolicy {
    public abstract double calculateFine(long daysLate);
}

class StandardFinePolicy extends FinePolicy {
    @Override
    public double calculateFine(long daysLate) {
        if (daysLate <= 0) return 0.0;
        return daysLate * 5.0;
    }
}

class VIPStudentFinePolicy extends FinePolicy {
    @Override
    public double calculateFine(long daysLate) {
        if (daysLate <= 0) return 0.0;
        return daysLate * 2.0;
    }
}