package EducationBorrowingSystem;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class Transaction {
    private String transactionId;
    private List<Borrowitem> borrowedItems;
    private Borrower borrower;
    private String purpose;
    private LocalDate checkoutDate;
    private LocalDate dueDate;
    private boolean isReturned;

    public Transaction(String transactionId, List<Borrowitem> borrowedItems, Borrower borrower, String purpose) {
        this.transactionId = transactionId;
        this.borrowedItems = borrowedItems;
        this.borrower = borrower;
        this.purpose = purpose;
        this.checkoutDate = LocalDate.now();
        this.dueDate = LocalDate.now().plusDays(7);
        this.isReturned = false;
    }

    public String getTransactionId() { return transactionId; }
    public List<Borrowitem> getBorrowedItems() { return borrowedItems; }
    public Borrower getBorrower() { return borrower; }
    public String getPurpose() { return purpose; }
    public LocalDate getCheckoutDate() { return checkoutDate; }
    public LocalDate getDueDate() { return dueDate; }
    public boolean isReturned() { return isReturned; }
    public void setReturned(boolean returned) { isReturned = returned; }

    public long daysLate() {
        return ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }

    public double processReturnAndCalculateFine() {
        FinePolicy policy;
        if (borrower.getSectionOrFacultyNo().toUpperCase().contains("STEM")) {
            policy = new VIPStudentFinePolicy();
        } else {
            policy = new StandardFinePolicy();
        }
        return policy.calculateFine(daysLate());
    }
}