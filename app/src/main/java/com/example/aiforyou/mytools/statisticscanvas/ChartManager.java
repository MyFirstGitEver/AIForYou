package com.example.aiforyou.mytools.statisticscanvas;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.example.aiforyou.mytools.CalculationPerCategory;
import com.example.aiforyou.mytools.statisticscanvas.stats.CategoryQuantifier;
import com.example.aiforyou.mytools.statisticscanvas.stats.SegmentQuantifier;

import java.util.HashMap;

public abstract class ChartManager {
    protected final ExcelReader reader;
    protected final int sheetNum;
    protected final String numericColName;
    protected final Pair<Float, Float> cord;
    public static final int[] colors = new int[]{
            Color.rgb(255, 0, 230),
            Color.BLUE, Color.RED, Color.GREEN, Color.CYAN, Color.DKGRAY, Color.MAGENTA,
            Color.rgb(0, 213, 255), Color.rgb(159, 2, 81),
            Color.rgb(225, 179, 120), Color.rgb(205, 179, 214), Color.YELLOW
    };

    protected static final float textPaddingY = 35, textPaddingX = 100;

    ChartManager(ExcelReader reader,
                 int sheetNum,
                 String numericColName,
                 Pair<Float, Float> cord) {
        this.reader = reader;
        this.sheetNum = sheetNum;
        this.numericColName = numericColName;
        this.cord = cord;
    }

    abstract public void draw(Canvas canvas, Paint painter);

    protected void drawHorizontalAxis(Paint painter, Canvas canvas, float startX, float y){
        painter.setColor(Color.BLACK);
        painter.setStrokeWidth(3);
        canvas.drawLine(startX, y, cord.first, y, painter);

        // draw triangle
        float b1 = cord.first - 30;
        float b2 = y - 30;

        float c1 = cord.first - 30;
        float c2 = y + 30;

        float d1 = cord.first;
        float d2 = y;

        Path path = new Path();
        path.moveTo(b1, b2);
        path.lineTo(c1, c2);
        path.lineTo(d1, d2);
        path.lineTo(b1, b2);
        canvas.drawPath(path, painter);
    }

    protected float scale(float x, float min, float max, float currMin, float currMax) {
        float distOfX = (x - min) / (max - min);

        return distOfX * (currMax - currMin) + currMin;
    }

    protected String shortenFloat(float f){
        if(f >= 1000 && f < 999_999){
            return Math.round(f / 1000) + "k";
        }
        else if(f >= 1_000_000 && f < 999_999_999){
            return Math.round(f / 10_000) / 100.0f + "m";
        }
        else if(f >= 1_000_000_000){
            return Math.round(f / 10_000_000) / 100.0f + "b";
        }
        else{
            return Float.toString(Math.round(f * 100) / 100.0f);
        }
    }
}

abstract class AxisChartManager extends ChartManager {
    protected final static float paddingX = 100, paddingY = 100;

    AxisChartManager(ExcelReader reader, int sheetNum, String numericColName, Pair<Float, Float> cord) {
        super(reader, sheetNum, numericColName, cord);
    }

    private String shortenString(String str){
        return str.substring(0, Math.min(4, str.length())) + "...";
    }

    private String[] convertToStrings(float[] floats){
        String[] strings = new String[floats.length];

        for(int i=0;i<strings.length;i++){
            strings[i] = shortenFloat(floats[i]);
        }

        return strings;
    }

    protected void plotInXAxis(Canvas canvas, Paint painter, Object tags, int range){
        float xRange = (cord.first - paddingX) / range;

        float[] floats = null;
        String[] strings = null;

        if(tags instanceof float[]){
            floats = (float[]) tags;
        }
        else{
            strings = (String[]) tags;
        }


        painter.setTextSize(35);
        painter.setColor(Color.parseColor("#2688B5"));

        for(int i=0;i<range;i++){
            float x0 = paddingX + xRange *(i + 1);

            canvas.drawCircle(x0, paddingY, 8, painter);
            if(floats != null){
                canvas.drawText(shortenFloat(floats[i]),
                        x0 - 80, paddingY - textPaddingY, painter);
            }
            else{
                canvas.drawText(shortenString(strings[i]),
                        x0 - 80, paddingY - textPaddingY, painter);
            }
        }
    }

    protected void plotInYAxis(Canvas canvas, Paint painter, float yRangeValue, int range){
        float yRange = (cord.second / 1.5f - paddingY) / range;

        float[] y = new float[range];

        for(int i=1;i<=range;i++){
            y[i - 1] = yRangeValue * i;
        }

        String[] yStrings = convertToStrings(y);

        painter.setTextSize(35);
        painter.setColor(Color.parseColor("#2688B5"));
        for(int i=0;i<range;i++){
            float y1 =  paddingY + yRange *(i + 1);

            canvas.drawCircle(paddingX, y1, 8, painter);
            canvas.drawText(yStrings[i],
                    paddingX - textPaddingX, y1 - (painter.descent() + painter.ascent()) / 2, painter);
        }
    }

    protected void drawVerticalAxis(Canvas canvas, Paint painter, float y) {
        painter.setColor(Color.BLACK);
        painter.setStrokeWidth(3);
        canvas.drawLine(paddingX, paddingY, paddingX, y, painter);

        // draw triangle
        float b1 = paddingX - 30;
        float b2 = y - 30;

        float c1 = paddingX + 30;
        float c2 = y - 30;

        float d1 = paddingX;
        float d2 = y;

        Path path = new Path();
        path.moveTo(b1, b2);
        path.lineTo(c1, c2);
        path.lineTo(d1, d2);
        path.lineTo(b1, b2);
        canvas.drawPath(path, painter);
    }

    protected void drawAxisSystem(Canvas canvas, Paint painter){
        painter.setStyle(Paint.Style.FILL_AND_STROKE);
        drawHorizontalAxis(painter, canvas,paddingX, paddingY);
        drawVerticalAxis(canvas, painter, cord.second / 1.5f + paddingY);
    }
}

class PieChartManager extends ChartManager{
    private final CategoryQuantifier[] items;
    private final float radius;
    private final CircleSolver solver;

    PieChartManager(ExcelReader reader,
                    int sheetNum,
                    String numericColName,
                    String categoryColName,
                    Pair<Float, Float> cord) throws IllegalArgumentException {
        super(reader, sheetNum, numericColName, cord);

        items = reader.getPieStats(categoryColName, numericColName, sheetNum);

        radius = cord.second / 4.0f;
        solver = new CircleSolver(new Pair<>(cord.first / 2.0, (double)radius + 10), radius);
    }

    @Override
    public void draw(Canvas canvas, Paint painter) {
        painter.setStrokeWidth(3f);
        painter.setTextSize(40);

        Pair<Double, Double>[] lines = new Pair[items.length];

        painter.setColor(Color.BLACK);
        for (int i = 0, itemsLength = items.length; i < itemsLength; i++) {
            CategoryQuantifier item = items[i];
            SolverAnswer estimator = solver.next(item.value * 360);
            lines[i] = estimator.lineInfo;

            painter.setColor(colors[i]);
            drawEllipse(estimator.arcInfo, canvas, painter);
        }

        painter.setColor(Color.GRAY);
        for(Pair<Double, Double> lineInfo : lines){
            canvas.drawLine(
                    solver.getCenter().first.floatValue(), solver.getCenter().second.floatValue(),
                    lineInfo.first.floatValue(), lineInfo.second.floatValue(), painter);
        }
    }

    private void drawEllipse(Pair<Double, Double> arcInfo, Canvas canvas, Paint painter){
        float startAngle = arcInfo.first.floatValue() - 90;

        if(startAngle < 0){
            startAngle += 360;
        }

        float sweepAngle = arcInfo.second.floatValue();

        Pair<Double, Double> center = solver.getCenter();
        canvas.drawArc(
                center.first.floatValue() - radius,
                center.second.floatValue() - radius,
                center.first.floatValue() + radius,
                center.second.floatValue() + radius,
                startAngle,
                sweepAngle,
                true,
                painter);
    }
}

class HistogramChartManager extends ChartManager{
    private final float[] items;
    private final CalculationPerCategory calculation;
    private final boolean usingStrength;
    private final static float padding = 40;
    private final static float topPadding = 60;

    HistogramChartManager(ExcelReader reader, int sheetNum, String numericColName, Pair<Float, Float> cord,
                          int range, boolean usingStrength) {
        super(reader, sheetNum, numericColName, cord);

        this.usingStrength = usingStrength;
        this.items = splitUpWithRange(range);
        this.calculation = null;
    }

    HistogramChartManager(ExcelReader reader,
                          int sheetNum,
                          String numericColName,
                          String categoryColName,
                          CalculationPerCategory calculation,
                          Pair<Float, Float> cord,
                          boolean usingStrength) {
        super(reader, sheetNum, numericColName, cord);

        this.usingStrength = usingStrength;
        CategoryQuantifier[] stats =
                reader.splitUpWithCategory(numericColName, categoryColName, sheetNum, calculation);
        this.calculation = calculation;

        items = new float[stats.length];
        int index = 0;

        for(CategoryQuantifier q : stats) {
            items[index] = q.value;
            index++;
        }
    }

    private float[] splitUpWithRange(int range) {
        double[] data = reader.getNumberListFromColumnInCell(numericColName, sheetNum);

        double min = Double.MAX_VALUE, max = Double.MIN_VALUE;

        for(double item : data){
            min = Math.min(item, min);
            max = Math.max(item, max);
        }

        double dist = (max - min) / range;

        float[] counters = new float[range];

        for(double d : data) {
            for(int i=1;i<=range;i++){
                double eps = Math.abs(d - dist * (i - 1));

                if((d > min + dist * (i - 1) || eps < 0.001) && d < min + dist * i){
                    counters[i - 1]++;
                    break;
                }

                eps = Math.abs(d - max);
                if(i == range && eps < 0.001){
                    counters[i - 1]++;
                }
            }
        }

        return counters;
    }

    @Override
    public void draw(Canvas canvas, Paint painter) {
        float barSize = Math.min(80, (cord.second / items.length - 2 * padding));

        float maxHeight = Float.MIN_VALUE;
        float total = 0;

        for(float critical : items) {
            total += critical;
        }

        for(float critical : items) {
            maxHeight = Math.max(scale(critical, 0, total, minScale, maxScale), maxHeight);
        }

        int index = 0;
        painter.setTextSize(cord.first / 30);
        float left = 100;

        for(float critical : items) {
            float height = scale(critical, 0, total, minScale, maxScale);

            float a = padding * (index + 1) + barSize * index;
            float b = maxHeight - height;

            if(!usingStrength){
                painter.setColor(colors[index]);
            }
            else{
                int aValue = (int) (critical / total * 255);
                aValue = (int) Math.min(Math.pow(aValue, 1.2), 255);
                aValue = Math.max(60, aValue);

                painter.setColor(Color.argb(aValue ,5, 107, 254));
            }

            canvas.drawRect(a, b + topPadding, a + barSize, maxHeight - 3 + topPadding, painter);

            if(calculation == null || calculation == CalculationPerCategory.ADD_UP){
                float percent = Math.round(critical / total * 100_00) / 100.0f;

                painter.setColor(Color.BLACK);
                if(index != items.length){
                    canvas.drawText( percent + " %", a, b + topPadding - 10, painter);
                }
                else{
                    canvas.drawText(left + " %", a, b + topPadding - 10, painter);
                }

                left -= percent;
            }
            else{
                painter.setColor(Color.BLACK);
                canvas.drawText(shortenFloat(critical), a, b + topPadding - 10, painter);
            }

            index++;
        }

        drawHorizontalAxis(painter, canvas, 0, maxHeight + topPadding);
    }

    private static final float minScale = 10;
    private static final float maxScale = 250;
}

class ScatterChart extends AxisChartManager{
    private final double[] x, y;
    private final String[] tags;
    private final float xRange, yRange;
    private final HashMap<String, Integer> indices;
    private final String categoryColName;

    ScatterChart(ExcelReader reader,
                 int sheetNum,
                 String numericColName,
                 String inPairWithColName,
                 String categoryColName,
                 Pair<Float, Float> cord) {
        super(reader, sheetNum, numericColName, cord);

        double[] x = reader.getNumberListFromColumnInCell(numericColName, sheetNum);
        double[] y = reader.getNumberListFromColumnInCell(inPairWithColName, sheetNum);
        String[] tags = reader.getStringListFromColumnInCell(categoryColName, sheetNum);

        float maxX = Float.MIN_VALUE;
        float maxY = Float.MIN_VALUE;

        int index = 0;
        for(int i=0;i<x.length;i++) {
            maxX = Math.max(maxX, (float)x[i]);
            maxY = Math.max(maxY, (float)y[i]);
        }

        this.xRange = maxX / 5;
        this.yRange = maxY / 5;

        if(tags != null) {
            indices = new HashMap<>();
            for (int i = 0; i < x.length; i++) {
                if (!indices.containsKey(tags[i])) {
                    indices.put(tags[i], index);
                    index++;
                }

                x[i] = scale((float) x[i], 0, maxX, paddingX , cord.first - 10.0f);
                y[i] = scale((float) y[i], 0, maxY, paddingY, cord.second / 1.5f);
            }
        }
        else {
            indices = null;
            for (int i = 0; i < x.length; i++) {
                x[i] = scale((float) x[i], 0, maxX, paddingX, cord.first - 10.0f);
                y[i] = scale((float) y[i], 0, maxY, paddingY, cord.second / 1.5f);
            }
        }

        this.x = x;
        this.y = y;

        this.tags = tags;
        this.categoryColName = categoryColName;
    }

    @Override
    public void draw(Canvas canvas, Paint painter) {
        painter.setColor(Color.BLACK);
        painter.setStyle(Paint.Style.STROKE);
        painter.setStrokeWidth(3);

        for(int i=0;i<x.length;i++){
            if(categoryColName != null){
                painter.setColor(colors[indices.get(tags[i])]);
            }

            canvas.drawCircle((float)x[i], (float)y[i], 10, painter);
        }

        drawAxisSystem(canvas, painter);

        float[] floats = new float[5];
        for(int i=0;i<5;i++){
            floats[i] = xRange * (i + 1);
        }

        plotInXAxis(canvas, painter, floats,  5);
        plotInYAxis(canvas, painter, yRange, 5);
    }
}

class SegmentChartManager extends AxisChartManager {
    private final float[][] counters;
    private final String[] allMainCats;
    private final float yRange;
    private final int numericRange;

    SegmentChartManager(ExcelReader reader, int sheetNum,
                        @NonNull  String mainCategory,
                        @NonNull  String subCategory,
                        String numericColName, Pair<Float, Float> cord, int numericRange) {
        super(reader, sheetNum, numericColName, cord);


        SegmentQuantifier[] segmentStats =
                reader.getSegmentStats(mainCategory, subCategory, numericColName, sheetNum);

        int m = segmentStats.length, n = segmentStats[0].getQuantifiers().length;

        counters = new float[m][n];
        allMainCats = new String[m];

        for(int i=0;i<m;i++) {
            allMainCats[i] = segmentStats[i].getCategoryName();
            float currTotal = 0;

            for(int j=0;j<n;j++) {
                currTotal += segmentStats[i].getQuantifiers()[j].value;
                counters[i][j] = currTotal;
            }
        }

        float max = Float.MIN_VALUE;

        for(int i=0;i<m;i++) {
            max = Math.max(max, counters[i][n - 1]);
        }

        yRange = max / numericRange;
        this.numericRange = numericRange;

        for(int i=0;i<counters.length;i++){
            for(int j=0;j<counters[0].length;j++){
                counters[i][j] = scale(counters[i][j], 0, max, paddingY, cord.second / 1.5f);
            }
        }
    }

    @Override
    public void draw(Canvas canvas, Paint painter) {
        drawAxisSystem(canvas, painter);
        plotInXAxis(canvas, painter, allMainCats, allMainCats.length);
        plotInYAxis(canvas, painter, yRange, numericRange);

        drawSegments(canvas, painter);
    }

    private void drawSegments(Canvas canvas, Paint painter){
        float xRange = (cord.first - paddingX) / allMainCats.length;

        painter.setStyle(Paint.Style.STROKE);
        for (int segmentNum = 0; segmentNum < counters[0].length; segmentNum++) {
            Path path = new Path();
            path.moveTo(paddingX + 3, paddingY + 3);
            painter.setColor(colors[segmentNum]);

            for (int i = 0; i < counters.length; i++) {
                path.lineTo(xRange * (i + 1) + paddingX, counters[i][segmentNum]);
            }

            canvas.drawPath(path, painter);
        }
    }
}