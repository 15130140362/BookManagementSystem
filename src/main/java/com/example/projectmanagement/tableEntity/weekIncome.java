package com.example.projectmanagement.tableEntity;


import lombok.Data;

@Data
public class weekIncome {
    private Integer MondayFine;
    private Integer MondayDeposit;
    private Integer TuesdayFine;
    private Integer TuesdayDeposit;
    private Integer WednesdayFine;
    private Integer WednesdayDeposit;
    private Integer ThursdayFine;
    private Integer ThursdayDeposit;
    private Integer FridayFine;
    private Integer FridayDeposit;
    private Integer SaturdayFine;
    private Integer SaturdayDeposit;
    private Integer SundayFine;
    private Integer SundayDeposit;
    private Integer TotalDeposit;
    private Integer TotalFine;
    private Integer TotalIncome;

    public Integer TotalDeposit(){
        return this.MondayDeposit+this.TuesdayDeposit+this.WednesdayDeposit
                +this.ThursdayDeposit+this.FridayDeposit+this.SaturdayDeposit+this.SundayDeposit;
    }
    public Integer TotalFine(){
        return this.MondayFine+this.TuesdayFine+this.WednesdayFine+this.ThursdayFine+
                this.FridayFine+this.SaturdayFine+this.SundayFine;
    }
    public Integer getTotalIncome(Integer fineIncome,Integer depositIncome){
        return fineIncome+depositIncome;
    }
}
