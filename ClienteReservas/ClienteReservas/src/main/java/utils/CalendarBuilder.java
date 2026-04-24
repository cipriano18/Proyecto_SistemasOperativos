/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.ColumnConstraints;
import model.CalendarBlock;

public class CalendarBuilder {

    private static final int COLUMNS = 5;

    public void buildCalendar(int month, int year, GridPane grid, List<CalendarBlock> blocks) {
        if (grid == null) {
            return;
        }

        grid.getChildren().clear();
        grid.getColumnConstraints().clear();
        grid.getRowConstraints().clear();

        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();

        configureColumns(grid);
        configureRows(grid, daysInMonth);

        DayCard dayCard = new DayCard();

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate localDate = LocalDate.of(year, month, day);
            Date sqlDate = Date.valueOf(localDate);

            int column = (day - 1) % COLUMNS;
            int row = (day - 1) / COLUMNS;

            grid.add(dayCard.createCard(day, sqlDate, blocks), column, row);
        }
    }

    private void configureColumns(GridPane grid) {
        for (int i = 0; i < COLUMNS; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setMinWidth(10);
            column.setPrefWidth(180);
            column.setHgrow(Priority.SOMETIMES);
            grid.getColumnConstraints().add(column);
        }
    }

    private void configureRows(GridPane grid, int daysInMonth) {
        int rows = (int) Math.ceil(daysInMonth / (double) COLUMNS);

        for (int i = 0; i < rows; i++) {
            RowConstraints row = new RowConstraints();
            row.setMinHeight(10);
            row.setVgrow(Priority.SOMETIMES);
            grid.getRowConstraints().add(row);
        }
    }
}
