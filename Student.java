package com.example.mealapp;

public class Student {
  private String firstName;
  private String lastName;
  private String className;
  private String classNumber;
  private String cardId;

  public Student(String firstName, String lastName, String className, String classNumber, String cardId) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.className = className;
    this.classNumber = classNumber;
    this.cardId = cardId;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getClassName() {
    return className;
  }

  public String getClassNumber() {
    return classNumber;
  }

  public String getCardId() {
    return cardId;
  }
}
