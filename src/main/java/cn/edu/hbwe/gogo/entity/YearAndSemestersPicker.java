package cn.edu.hbwe.gogo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;

@Data
@AllArgsConstructor
public class YearAndSemestersPicker {
    private HashMap<String,String> years;
    private HashMap<String,String> semesters;
    private Term defaultTerm;
}
