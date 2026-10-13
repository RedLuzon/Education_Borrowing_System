package EducationBorrowingSystem;

public class Borrower {
    private String borrowerName;
    private String sectionOrFacultyNo;

    public Borrower(String borrowerName, String sectionOrFacultyNo) {
        this.borrowerName = borrowerName;
        this.sectionOrFacultyNo = sectionOrFacultyNo;
    }

    public String getBorrowerName() { return borrowerName; }
    public void setBorrowerName(String borrowerName) { this.borrowerName = borrowerName; }

    public String getSectionOrFacultyNo() { return sectionOrFacultyNo; }
    public void setSectionOrFacultyNo(String sectionOrFacultyNo) { this.sectionOrFacultyNo = sectionOrFacultyNo; }
}