package fi.dy.masa.malilib.data;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import fi.dy.masa.malilib.MaLiLib;

public class DataDump
{
    public static final String EMPTY_STRING = "";

    protected final int columns;
    protected Alignment[] alignment;
    protected boolean[] columnIsNumeric;
    protected Row title;
    protected List<Row> headers = new ArrayList<>();
    protected List<Row> footers = new ArrayList<>();
    protected List<Row> lines = new ArrayList<>();
    protected int[] widths;
    protected int totalWidth;
    protected String formatStringColumns;
    protected String formatStringSingleCenter;
    protected String formatStringSingleLeft;
    protected String formatStringTitleCSV;
    protected String formatStringSingleLeftCSV;
    protected String lineSeparator;
    protected boolean useColumnSeparator = false;
    protected boolean centerTitle = false;
    protected boolean repeatTitleAtBottom = true;
    private boolean sort = true;
    private Format format;

    public DataDump(int columns)
    {
        this(columns, Format.ASCII);
    }

    public DataDump(int columns, Format format)
    {
        this.columns = columns;
        this.format = format;
        this.alignment = new Alignment[this.columns];
        this.columnIsNumeric = new boolean[this.columns];
        this.widths = new int[this.columns];

        Arrays.fill(this.alignment, Alignment.LEFT);
        Arrays.fill(this.columnIsNumeric, false);
    }

    public DataDump setColumnProperties(int columnId, Alignment align, boolean isNumeric)
    {
        this.setColumnAlignment(columnId, align);
        this.columnIsNumeric[columnId] = isNumeric;

        return this;
    }

    public DataDump setColumnAlignment(int columnId, Alignment align)
    {
        if (columnId >= this.columns)
        {
            throw new IllegalArgumentException("setColumnAlignment(): Invalid column id '" + columnId + "', max is " + (this.columns - 1));
        }

        this.alignment[columnId] = align;
        return this;
    }

    public DataDump setColumnIsNumeric(int columnId, boolean isNumeric)
    {
        if (columnId >= this.columns)
        {
            throw new IllegalArgumentException("setColumnIsNumeric(): Invalid column id '" + columnId + "', max is " + (this.columns - 1));
        }

        this.columnIsNumeric[columnId] = isNumeric;
        return this;
    }

    public Format getFormat()
    {
        return this.format;
    }

    public void setFormat(Format format)
    {
        this.format = format;
    }

    public void setSort(boolean sort)
    {
        this.sort = sort;
    }

    public void setCenterTitle(boolean center)
    {
        this.centerTitle = center;
    }

    public void setRepeatTitleAtBottom(boolean repeat)
    {
        this.repeatTitleAtBottom = repeat;
    }

    public void setUseColumnSeparator(boolean value)
    {
        this.useColumnSeparator = value;
    }

    public void addTitle(String... data)
    {
        if (this.checkHeaderData(data))
        {
            this.title = new Row(data);
        }
    }

    public void addHeader(String... data)
    {
        //this.checkData(data);
        this.headers.add(new Row(data));
    }

    public void addHeader(int index, String... data)
    {
        //this.checkData(data);
        this.headers.add(index, new Row(data));
    }

    public void addFooter(String... data)
    {
        //this.checkData(data);
        this.footers.add(new Row(data));
    }

    public void addData(String... data)
    {
        if (this.checkData(data))
        {
            this.lines.add(new Row(data));
        }
    }

    private boolean checkHeaderData(String... data)
    {
        if (data.length != 1 || this.columns == 1)
        {
            return this.checkData(data);
        }

        return false;
    }

    private void checkAllHeaders()
    {
        if (this.format == Format.ASCII && this.columns != 1)
        {
            if (this.title != null)
            {
                this.checkHeaderLength(this.title);
            }

            int size = this.headers.size();
            for (int i = 0; i < size; i++)
            {
                this.checkHeaderLength(this.headers.get(i));
            }

            size = this.footers.size();
            for (int i = 0; i < size; i++)
            {
                this.checkHeaderLength(this.footers.get(i));
            }
        }
    }

    private void checkHeaderLength(Row row)
    {
        String[] values = row.getValues();

        if (values.length == 1)
        {
            this.checkHeaderLength(values[0]);
        }
    }

    private void checkHeaderLength(String header)
    {
        int len = header.length();
        int columns = this.widths.length;
        int space = this.totalWidth + (Math.max(columns - 1, 0) * 3);

        // The title is longer than all the columns and padding character put together,
        // so we will add to the last column's width enough to widen the entire table enough to fit the header.
        if (len > space)
        {
            int diff = len - space;
            this.widths[this.widths.length - 1] += diff;
            this.totalWidth += diff;
        }
    }

    private boolean checkData(String... data)
    {
        if (data.length != this.columns && data.length != 1)
        {
            throw new IllegalArgumentException("Invalid number of columns, you must add exactly " +
                    this.columns + " columns for this type of DataDump");
        }

        int total = 0;
        boolean valid = true;

        for (int i = 0; i < data.length; i++)
        {
            if (data[i] == null)
            {
                MaLiLib.LOGGER.warn("null value at column index {} on row '{}'", i, this.rowDataToString(data));
                valid = false;
            }
            else if (this.format == Format.ASCII)
            {
                int len = data[i].length();

                int width = this.widths[i];

                if (len > width)
                {
                    this.widths[i] = len;
                }

                total += this.widths[i];
            }
        }

        if (this.format == Format.ASCII && total > this.totalWidth)
        {
            this.totalWidth = total;
        }

        return valid;
    }

    private String rowDataToString(String... data)
    {
        return String.join(", ", data);
    }

    protected void generateFormatStrings()
    {
        if (this.format == Format.ASCII)
        {
            this.generateFormatStringsASCII();
        }
        else if (this.format == Format.CSV)
        {
            this.generateFormatStringsCSV();
        }
    }

    private String getFormattedLine(Row row)
    {
        if (this.format == Format.ASCII)
        {
            return this.getFormattedLineASCII(row);
        }
        else if (this.format == Format.CSV)
        {
            return this.getFormattedLineCSV(row, this.formatStringColumns);
        }

        return EMPTY_STRING;
    }

    private String getFormattedTitle(Row row)
    {
        if (this.format == Format.ASCII)
        {
            return this.getFormattedLineASCII(row);
        }
        else if (this.format == Format.CSV)
        {
            // The title row in CSV needs to be quoted on all columns
            return this.getFormattedLineCSV(row, this.formatStringTitleCSV);
        }

        return EMPTY_STRING;
    }

    protected void generateFormatStringsASCII()
    {
        this.checkAllHeaders();

        String colSep = this.useColumnSeparator ? "|" : " ";
        String lineColSep = this.useColumnSeparator ? "+" : "-";
        StringBuilder sbFmt = new StringBuilder(128);
        StringBuilder sbSep = new StringBuilder(256);
        sbFmt.append(colSep);
        sbSep.append(lineColSep);

        for (int i = 0; i < this.columns; i++)
        {
            int width = this.widths[i];

            if (this.alignment[i] == Alignment.LEFT)
            {
                sbFmt.append(String.format(" %%-%ds %s", width, colSep));
            }
            else
            {
                sbFmt.append(String.format(" %%%ds %s", width, colSep));
            }

            sbSep.append("-".repeat(Math.max(0, width + 2)));
            sbSep.append(lineColSep);
        }

        this.formatStringColumns = sbFmt.toString();
        this.lineSeparator = sbSep.toString();
        this.formatStringSingleCenter = colSep + " %%%ds%%s%%%ds " + colSep;
        this.formatStringSingleLeft = colSep + " %%-%ds " + colSep;
    }

    private String getFormattedLineASCII(Row row)
    {
        Object[] values = row.getValues();

        if (values.length == 1 && this.columns > 1)
        {
            int space = this.totalWidth + (Math.max(this.columns - 1, 0) * 3);
            String fmt;
            boolean isCenter = false;

            if (this.centerTitle)
            {
                String str = String.valueOf(values[0]);
                int len = str.length();
                int start = (space - len) / 2;

                if (start > 0)
                {
                    fmt = String.format(this.formatStringSingleCenter, start, space - len - start);
                    isCenter = true;
                }
                else
                {
                    fmt = String.format(this.formatStringSingleLeft, space);
                }
            }
            else
            {
                fmt = String.format(this.formatStringSingleLeft, space);
            }

            return isCenter ? String.format(fmt, " ", values[0], " ") : String.format(fmt, values[0]);
        }

        return String.format(this.formatStringColumns, values);
    }

    protected void generateFormatStringsCSV()
    {
        StringBuilder sbFmtTitle = new StringBuilder(128);
        StringBuilder sbFmtColumns = new StringBuilder(128);
        StringBuilder sbFmtSingleLeft = new StringBuilder(128);
        final String fmtNumeric = "%s";
        final String fmtString = "\"%s\"";

        sbFmtSingleLeft.append(this.columnIsNumeric[0] ? fmtNumeric : fmtString);

        for (int i = 0; i < this.columns; i++)
        {
            sbFmtTitle.append(fmtString);
            sbFmtColumns.append(this.columnIsNumeric[i] ? fmtNumeric : fmtString);

            if (i < (this.columns - 1))
            {
                sbFmtTitle.append(",");
                sbFmtColumns.append(",");
                sbFmtSingleLeft.append(",");
            }
        }

        this.formatStringTitleCSV = sbFmtTitle.toString();
        this.formatStringColumns = sbFmtColumns.toString();
        this.lineSeparator = EMPTY_STRING;
        this.formatStringSingleCenter = EMPTY_STRING;
        this.formatStringSingleLeftCSV = sbFmtSingleLeft.toString();
    }

    private String getFormattedLineCSV(Row row, String formatStringColumns)
    {
        String[] valuesStr = row.getValues();
        Object[] valuesObj = new Object[valuesStr.length];

        for (int i = 0; i < valuesObj.length; i++)
        {
            // Fix the values so that they don't break the CSV format,
            // ie. double any quotes (escape quotes with a quote).
            // Note that all non-numeric columns (ie. strings) are already being surrounded
            // in double quotes by the format string.
            valuesStr[i] = valuesStr[i].replace("\"", "\"\"");

            // Numeric columns are not surrounded in double quotes by default, so if there are
            // any commas in those, then we need to double quote in those cases.
            if (this.columnIsNumeric[i] && valuesStr[i].contains(","))
            {
                valuesStr[i] = "\"" + valuesStr[i] + "\"";
            }

            valuesObj[i] = valuesStr[i].trim();
        }

        if (valuesObj.length == 1 && this.columns > 1)
        {
            return String.format(this.formatStringSingleLeftCSV, valuesObj[0]);
        }
        else
        {
            return String.format(formatStringColumns, valuesObj);
        }
    }

    protected List<String> getFormattedData(List<String> lines)
    {
        if (this.sort)
        {
            Collections.sort(this.lines);
        }

        if (this.format == Format.ASCII)
        {
            lines.add(this.lineSeparator);

            int len = this.headers.size();

            if (len > 0)
            {
                for (Row header : this.headers)
                {
                    lines.add(this.getFormattedLine(header));
                }

                lines.add(this.lineSeparator);
            }
        }

        if (this.title != null)
        {
            lines.add(this.getFormattedTitle(this.title));

            if (this.format == Format.ASCII)
            {
                lines.add(this.lineSeparator);
            }
        }

        for (Row line : this.lines)
        {
            lines.add(this.getFormattedLine(line));
        }

        if (this.format == Format.ASCII)
        {
            if (this.repeatTitleAtBottom && this.title != null)
            {
                lines.add(this.lineSeparator);
                lines.add(this.getFormattedLine(this.title));
            }

            lines.add(this.lineSeparator);
            final int len = this.footers.size();

            if (len > 0)
            {
                for (Row footer : this.footers)
                {
                    lines.add(this.getFormattedLine(footer));
                }

                lines.add(this.lineSeparator);
            }
        }

        return lines;
    }

    public List<String> getLines()
    {
        List<String> lines = new ArrayList<>();

        this.generateFormatStrings();
        this.getFormattedData(lines);

        return lines;
    }

    @Deprecated
    @Nullable
    public static File dumpDataToFile(File dir, String fileNameBase, List<String> lines, Format format)
    {
        Path dirPath = Paths.get(dir.toURI());
        Path out;

        if (format == Format.CSV)
        {
            out = dumpDataToFile(dirPath, fileNameBase + "-csv", ".csv", lines);
        }
        else
        {
            out = dumpDataToFile(dirPath, fileNameBase, ".txt", lines);
        }

        if (out != null)
        {
            return out.toFile();
        }

        return null;
    }

    @Deprecated
    @Nullable
    public static File dumpDataToFile(File dir, String fileNameBase, List<String> lines)
    {
        Path dirPath = Paths.get(dir.toURI());
        Path out = dumpDataToFile(dirPath, fileNameBase, ".txt", lines);

        if (out != null)
        {
            return out.toFile();
        }

        return null;
    }

    @Deprecated
    @Nullable
    public static File dumpDataToFile(File dir, String fileNameBase, String fileNameExtension, List<String> lines)
    {
        Path dirPath = Paths.get(dir.toURI());

        Path out = dumpDataToFile(dirPath, fileNameBase, fileNameExtension, lines);

        if (out != null)
        {
            return out.toFile();
        }

        return null;
    }

    @Nullable
    public static Path dumpDataToFile(Path dir, String fileNameBase, List<String> lines, Format format)
    {
        if (format == Format.CSV)
        {
            return dumpDataToFile(dir, fileNameBase + "-csv", ".csv", lines);
        }
        else
        {
            return dumpDataToFile(dir, fileNameBase, ".txt", lines);
        }
    }

    @Nullable
    public static Path dumpDataToFile(Path dir, String fileNameBase, List<String> lines)
    {
        return dumpDataToFile(dir, fileNameBase, ".txt", lines);
    }

    @Nullable
    public static Path dumpDataToFile(Path dir, String fileNameBase, String fileNameExtension, List<String> lines)
    {
        Path outFile = null;

        if (!Files.exists(dir))
        {
            try
            {
                Files.createDirectory(dir);
            }
            catch (Exception e)
            {
                MaLiLib.LOGGER.error("dumpDataToFile(): Failed to create the configuration directory", e);
                return null;
            }
        }

        String fileNameBaseWithDate = fileNameBase + "_" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date(System.currentTimeMillis()));
        String fileName = fileNameBaseWithDate + fileNameExtension;
        outFile = dir.resolve(fileName);
        int postFix = 1;

        while (Files.exists(outFile) && postFix < 100)
        {
            fileName = fileNameBaseWithDate + "_" + postFix + fileNameExtension;
            outFile = dir.resolve(fileName);
            postFix++;
        }

        if (Files.exists(outFile))
        {
            MaLiLib.LOGGER.error("dumpDataToFile(): Failed to create data dump file '{}', one already exists", fileName);
            return null;
        }

        try
        {
            Files.createFile(outFile);
        }
        catch (IOException e)
        {
            MaLiLib.LOGGER.error("dumpDataToFile(): Failed to create data dump file '{}'", fileName, e);
            return null;
        }

        try
        {
            for (String line : lines)
            {
                Files.writeString(outFile, line + System.lineSeparator(), StandardOpenOption.APPEND);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.error("dumpDataToFile(): Exception while writing data dump to file '{}'", fileName, e);
        }

        return outFile;
    }

    public static void printDataToLogger(List<String> lines)
    {
        for (String line : lines)
        {
            MaLiLib.LOGGER.info(line);
        }
    }

    public static class Row implements Comparable<Row>
    {
        private final String[] strings;
        private final Double[] numbers;

        public Row(String[] strings)
        {
            this.strings = strings;
            this.numbers = new Double[strings.length];

            for (int i = 0; i < strings.length; i++)
            {
                try
                {
                    this.numbers[i] = Double.parseDouble(strings[i]);
                }
                catch (NumberFormatException ignored) {}
            }
        }

        public String[] getValues()
        {
            return this.strings;
        }

        @Override
        public int compareTo(@Nonnull Row other)
        {
            for (int i = 0; i < this.strings.length; i++)
            {
                if (this.numbers[i] != null && other.numbers[i] != null)
                {
                    double d1 = this.numbers[i];
                    double d2 = other.numbers[i];

                    if (d1 < d2)
                    {
                        return -1;
                    }
                    else if (d1 > d2)
                    {
                        return 1;
                    }
                }
                else
                {
                    int res = this.strings[i].compareTo(other.strings[i]);

                    if (res != 0)
                    {
                        return res;
                    }
                }
            }

            return 0;
        }
    }

    public enum Alignment
    {
        LEFT,
        RIGHT
    }

    public enum Format
    {
        ASCII,
        CSV
    }
}
