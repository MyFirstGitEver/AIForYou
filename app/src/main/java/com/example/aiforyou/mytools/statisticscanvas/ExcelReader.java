package com.example.aiforyou.mytools.statisticscanvas;

import android.util.Pair;

import com.example.aiforyou.mytools.CalculationPerCategory;
import com.example.aiforyou.mytools.statisticscanvas.stats.CategoryQuantifier;
import com.example.aiforyou.mytools.statisticscanvas.stats.SegmentQuantifier;
import com.example.aiforyou.custom.ProjectDTO;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;


public class ExcelReader {
    private final XSSFWorkbook workbook;

    public ExcelReader(InputStream stream) throws IOException {
        workbook = new XSSFWorkbook(stream);
    }

    public double[] getNumberListFromColumnInCell(Object colId, int sheetNum) throws IllegalStateException {
        XSSFSheet sheet = workbook.getSheetAt(sheetNum);

        int colNum;
        if(colId instanceof String) {
            colNum = mFindHeaderIn(sheet, (String) colId);
        }
        else {
            colNum = (int) colId;
        }

        if(colNum == -1) {
            return null;
        }

        double[] doubles = new double[sheet.getLastRowNum()];

        for(int i=1;i<sheet.getPhysicalNumberOfRows();i++){
            XSSFRow row = sheet.getRow(i);
            if(row == null || row.getCell(colNum) == null){
                continue;
            }

            doubles[i - 1] = row.getCell(colNum).getNumericCellValue();
        }

        return doubles;
    }

    public CategoryQuantifier[] getPieStats(String categoryColName, String numericColName, int sheetNum) throws IllegalArgumentException{
        String[] cats = getStringListFromColumnInCell(categoryColName, sheetNum);
        double[] data = getNumberListFromColumnInCell(numericColName, sheetNum);

        HashMap<String, Double> hm = new HashMap<>();

        double total = 0;
        for(int i=0;i<data.length;i++) {
            if(hm.get(cats[i]) == null) {
                hm.put(cats[i], data[i]);
                if(hm.size() > ChartManager.colors.length) {
                    throw new IllegalArgumentException("Two many categories");
                }
            }
            else{
                hm.put(cats[i], data[i] + hm.get(cats[i]));
            }

            total += data[i];
        }

        int index = 0;
        CategoryQuantifier[] items = new CategoryQuantifier[hm.size()];
        for(Map.Entry<String, Double> e : hm.entrySet()){
            items[index] = new CategoryQuantifier(e.getKey(), (float) (e.getValue() / total));
            index++;
        }

        return items;
    }

    public CategoryQuantifier[] splitUpWithCategory(
            String numericColName,
            String categoryColName,
            int sheetNum,
            CalculationPerCategory calculation) {

        double[] data = getNumberListFromColumnInCell(numericColName, sheetNum);
        String[] cats = getStringListFromColumnInCell(categoryColName, sheetNum);

        HashMap<String, Integer> hm = new HashMap<>();

        int index = 0;
        for(int i=0;i<data.length;i++) {
            if(!hm.containsKey(cats[i])) {
                hm.put(cats[i], index);
                index++;
            }
        }

        float[][] custom = new float[hm.size()][4];
        float[] countAvg = new float[hm.size()];

        for(int i=0;i<custom.length;i++) {
            custom[i][CalculationPerCategory.ADD_UP.ordinal()] = 0;
            custom[i][CalculationPerCategory.LOWEST.ordinal()] = Float.MAX_VALUE;
            custom[i][CalculationPerCategory.HIGHEST.ordinal()] = Float.MIN_VALUE;
            custom[i][CalculationPerCategory.AVG.ordinal()] = 0;
        }

        for(int i=0;i<data.length;i++) {
            int type = hm.get(cats[i]);

            custom[type][CalculationPerCategory.ADD_UP.ordinal()] += data[i];
            custom[type][CalculationPerCategory.LOWEST.ordinal()] = (float) Math.min(data[i],
                    custom[type][CalculationPerCategory.LOWEST.ordinal()]);

            custom[type][CalculationPerCategory.HIGHEST.ordinal()] = (float) Math.max(data[i],
                    custom[type][CalculationPerCategory.HIGHEST.ordinal()]);

            countAvg[type]++;
        }

        CategoryQuantifier[] items = new CategoryQuantifier[custom.length];

        for(Map.Entry<String, Integer> entry : hm.entrySet()) {
            if(calculation == CalculationPerCategory.AVG) {
                items[entry.getValue()] = new CategoryQuantifier(entry.getKey(),
                        custom[entry.getValue()][3] / countAvg[3]);
            }
            else {
                items[entry.getValue()] = new CategoryQuantifier(entry.getKey(),
                        custom[entry.getValue()][calculation.ordinal()]);
            }
        }

        return items;
    }

    public String[] getScatterStats(ExcelReader reader, String categoryColName, int sheetNum) {
        String[] tags = reader.getStringListFromColumnInCell(categoryColName, sheetNum);

        HashSet<String> indices = new HashSet<>();
        List<String> unique = new ArrayList<>();

        for (String tag : tags) {
            if (!indices.contains(tag)) {
                indices.add(tag);
                unique.add(tag);
            }
        }

        return unique.toArray(new String[0]);
    }

    public SegmentQuantifier[] getSegmentStats(String mainCategory, String subCategory, String numericColName,
                                             int sheetNum) {

        String[] allMainCats = filterDistinct(getStringListFromColumnInCell(mainCategory, sheetNum));
        String[] allSubCats = filterDistinct(getStringListFromColumnInCell(subCategory, sheetNum));

        double[] data = getNumberListFromColumnInCell(numericColName, sheetNum);

        int j = 0;

        SegmentQuantifier[] quantifiers = new SegmentQuantifier[allMainCats.length];

        for(String mainCat : allMainCats) {
            int i = 0;
            CategoryQuantifier[] categoryQuantifiers = new CategoryQuantifier[allSubCats.length];

            for(String subCat : allSubCats) {
                float totalInSub = 0;

                for(int a=0;a<data.length;a++){
                    String thisMainCat = getStringAt(sheetNum, a, mainCategory);
                    String thisSubCat = getStringAt(sheetNum, a, subCategory);

                    if(thisMainCat.equals(mainCat) && thisSubCat.equals(subCat)){
                        totalInSub += data[a];
                    }
                }

                categoryQuantifiers[i] = new CategoryQuantifier(subCat, totalInSub);
                i++;
            }

            quantifiers[j] = new SegmentQuantifier(mainCat, categoryQuantifiers);

            j++;
        }

        return quantifiers;
    }

    public String[] getStringListFromColumnInCell(Object colId, int sheetNum) {
        XSSFSheet sheet = workbook.getSheetAt(sheetNum);
        int colNum;
        if(colId instanceof String) {
            colNum = mFindHeaderIn(sheet, (String) colId);
        }
        else {
            colNum = (int) colId;
        }

        if(colNum == -1){
            return null;
        }

        String[] strings = new String[sheet.getLastRowNum()];

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        for(int i=1;i<sheet.getPhysicalNumberOfRows();i++){
            XSSFRow row = sheet.getRow(i);

            if(row == null || row.getCell(colNum) == null){
                continue;
            }

            if(row.getCell(colNum).getCellType() == CellType.STRING) {
                strings[i - 1] = row.getCell(colNum).getStringCellValue().trim();
            }
            else if(DateUtil.isCellDateFormatted(row.getCell(colNum))) {
                strings[i - 1] = formatter.format(row.getCell(colNum).getDateCellValue());
            }
            else {
                strings[i - 1] = Double.toString(row.getCell(colNum).getNumericCellValue());
            }
        }

        int cutoff = -1;
        for(int i=strings.length -1;i>=0;i--){
            if(strings[i] != null){
                cutoff = i;
                break;
            }
        }

        return Arrays.copyOfRange(strings, 0, cutoff + 1);
    }

    public String getDataFromColumn(int rowNum, int colNum) {
        XSSFSheet sheet = workbook.getSheetAt(0);

        XSSFCell cell = sheet.getRow(rowNum).getCell(colNum);
        if(cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        }
        else {
            return Double.toString(cell.getNumericCellValue());
        }
    }

    public CellType getTypeInColumn(int colNum, int sheetNum) {
        return workbook.getSheetAt(sheetNum).getRow(1).getCell(colNum).getCellType();
    }

    public Pair<ProjectDTO.ProjectType, String[]> toolQuery(String query) {
        query = query.toLowerCase();

        // open canvas or loading ML
        int pos = query.indexOf('(');
        int pos2 = query.indexOf(')');

        String tool = query.substring(1, pos);

        return new Pair<>(
                ProjectDTO.ProjectType.valueOf(tool.toUpperCase()), query.substring(pos + 1, pos2).split(", "));
    }

    public String[] query(int sheetNum, String query, String colName) {
        query = query.toLowerCase();
        colName = colName.toLowerCase();

        if(query.equals("")) {
            return getStringListFromColumnInCell(colName, sheetNum);
        }

        String[] queries;
        if(!query.contains("&")) {
            queries = new String[1];
            queries[0] = query;
        }
        else{
            queries = query.split(" & ");
        }

        XSSFSheet sheet = workbook.getSheetAt(sheetNum);
        int numberOfRows = sheet.getPhysicalNumberOfRows();
        List<Integer> indices = new ArrayList<>(numberOfRows);

        for(int i=1;i<numberOfRows;i++){
            indices.add(i);
        }

        int colNum = mFindHeaderIn(sheet, colName);
        CellType colType  = mGetColumnTypeIn(sheet, colName);

        for(String q : queries) {
            String[] components = q.split(" ");

            switch (components[0].toLowerCase()){
                case "first":
                    // Ex: First 3
                    indices = indices.subList(0, Integer.parseInt(components[1]));
                    break;
                case "from":
                    // Ex: From 3 to 4
                    indices = indices.subList(Integer.parseInt(components[1]) - 1, Integer.parseInt(components[3]));
                    break;
                case "with":
                    // Ex: with Salary > 10k

                    CellType refColType = mGetColumnTypeIn(sheet, components[1]);

                    if(refColType == CellType.NUMERIC){
                        indices = mOperatorQueryHandlerForNumberColumn(components[2], components[3], indices,
                                sheetNum, components[1]);
                    }
                    else if(refColType == CellType.STRING) {
                        StringBuilder searchTerm = new StringBuilder();

                        for(int i=3;i<components.length;i++) {
                            searchTerm.append(components[i]);

                            if(i != components.length - 1) {
                                searchTerm.append(" ");
                            }
                        }

                        indices = mOperatorQueryHandlerForStringColumn(components[2],
                                searchTerm.toString().toLowerCase(), indices,
                                sheetNum, components[1]);
                    }
                    break;
                case "sort":
                    int sortByColNum = mFindHeaderIn(sheet, components[1]);
                    CellType refColType2 = colType;

                    if(components.length == 2) {
                        sortByColNum = mFindHeaderIn(sheet, components[1]);
                        refColType2 = mGetColumnTypeIn(sheet, components[1]);
                    }

                    List<Item> items = new ArrayList<>();

                    for(int i : indices){
                        try{
                            if(refColType2 == CellType.NUMERIC){
                                items.add(new Item(sheet.getRow(i).getCell(sortByColNum).getNumericCellValue(), i));
                            }
                            else{
                                items.add(new Item(sheet.getRow(i).getCell(sortByColNum).getStringCellValue(), i));
                            }
                        }
                        catch (Exception e){
                            break;
                        }
                    }

                    Collections.sort(items);
                    indices.clear();

                    for(Item item : items){
                        indices.add(item.position);
                    }
                    break;
                default:
                    // Ex: >= 35k;

                    if(colType == CellType.NUMERIC){
                        indices = mOperatorQueryHandlerForNumberColumn(components[0], components[1], indices,
                                sheetNum, colName);
                    }
                    else if(colType == CellType.STRING || colType == CellType.BOOLEAN){
                        indices = mOperatorQueryHandlerForStringColumn(components[0], components[1], indices,
                                sheetNum, colName);
                    }
            }
        }

        String[] data = new String[indices.size()];

        DataFormatter formatter = new DataFormatter();

        for(int i=0;i<indices.size();i++) {
            int index = indices.get(i);
            data[i] = formatter.formatCellValue(sheet.getRow(index).getCell(colNum));
        }

        return data;
    }

    public String getStringAt(int sheetNum, int rowNum, String colName){
        int colNum = mFindHeaderIn(workbook.getSheetAt(sheetNum), colName);

        XSSFCell cell = workbook.getSheetAt(sheetNum).getRow(rowNum + 1).getCell(colNum);

        String original;
        if(cell.getCellType() == CellType.STRING) {
            original = cell.getStringCellValue();
        }
        else {
            original = Double.toString(cell.getNumericCellValue());
        }

        return original.trim();
    }

    public String getColName(int sheetNum, int colNum) {
        return workbook.getSheetAt(sheetNum).getRow(0).getCell(colNum).getStringCellValue();
    }

    public int getColId(int sheetNum, String col) {
        String[] headers = getHeaders(sheetNum);

        for(int i=0;i<headers.length;i++) {
            if(headers[i].toLowerCase().equals(col)) {
                return i;
            }
        }

        return -1;
    }

    public String[] getHeaders(int sheetNum) {
        int colNum = workbook.getSheetAt(sheetNum).getRow(0).getLastCellNum();

        String[] headers = new String[colNum];
        for(int i=0;i<colNum;i++) {
            headers[i] = workbook.getSheetAt(sheetNum).getRow(0).getCell(i).getStringCellValue().toLowerCase();
        }

        return headers;
    }

    private String[] filterDistinct(String[] all){
        HashSet<String> set = new HashSet<>();

        Collections.addAll(set, all);

        String[] filtered = new String[set.size()];

        int index = 0;
        for(String s : set){
            filtered[index] = s;
            index++;
        }

        return filtered;
    }

    private List<Integer> mOperatorQueryHandlerForStringColumn(String operator, String searchTerm,
                                                               List<Integer> indices, int sheetNum, String colName) {
        XSSFSheet sheet = workbook.getSheetAt(sheetNum);
        int colNum = mFindHeaderIn(sheet, colName);

        List<Integer> ints = new ArrayList<>();

        for(int i : indices) {
            Row row = sheet.getRow(i);

            if(row == null || row.getCell(colNum) == null){
                continue;
            }

            String value = row.getCell(colNum).getStringCellValue().toLowerCase();

            if(mConditionMatched(operator, value.compareTo(searchTerm))){
                ints.add(i);
            }
        }

        return ints;
    }

    private List<Integer> mOperatorQueryHandlerForNumberColumn(String operator, String searchTerm,
                                                               List<Integer> indices, int sheetNum, String colName){
        XSSFSheet sheet = workbook.getSheetAt(sheetNum);
        int colNum = mFindHeaderIn(sheet, colName);

        List<Integer> ints = new ArrayList<>();

        for(int i : indices){
            Row row = sheet.getRow(i);

            if(row == null || row.getCell(colNum) == null){
                continue;
            }

            double value = row.getCell(colNum).getNumericCellValue();

            if(mConditionMatched(operator, (int)(value - Double.parseDouble(searchTerm)))){
                ints.add(i);
            }
        }

        return ints;
    }

    private boolean mConditionMatched(String operator, int compareResult){
        return (compareResult < 0 && operator.contains("<")) ||
                (compareResult == 0 && operator.contains("=")) ||
                (compareResult > 0 && operator.contains(">"));
    }

    private int mFindHeaderIn(XSSFSheet sheet, String headerName){
        XSSFRow headerRow = sheet.getRow(0);

        int colNum = -1;
        for(Cell cell : headerRow){
            String columnName = cell.getStringCellValue().toLowerCase();

            if(columnName.equals(headerName)){
                colNum = cell.getColumnIndex();
                break;
            }
        }

        return colNum;
    }

    private CellType mGetColumnTypeIn(XSSFSheet sheet, String colName){
        int colNum = mFindHeaderIn(sheet, colName);

        return sheet.getRow(1).getCell(colNum).getCellType();
    }

    private static class Item implements Comparable<Item>{
        Object value;
        int position;

        Item(Object value, int position){
            this.value = value;
            this.position = position;
        }

        @Override
        public int compareTo(Item o) {
            if(value instanceof String){
                return ((String)value).compareTo((String) o.value);
            }
            else{
                return (int)((Double) value - (Double) o.value);
            }
        }
    }
}