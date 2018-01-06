package com.example.android.calculator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    View[] buttonViews = new View[24];
    TextView textBox;

    // lists of numbers and characters for comparative purposes
    private String[] funcs = {"*", "/", "x", "+", "-", "÷"};
    private String[] nums = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "."};
    private ArrayList<String> functions = new ArrayList<>(Arrays.asList(funcs));
    private ArrayList<String> numbers = new ArrayList<>(Arrays.asList(nums));

    private ArrayList<String> stream = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textBox = (TextView) findViewById(R.id.text_box);

        setUpViewArray();
        ArrayList<Button> buttons = new ArrayList<>();
        ButtonListener buttonListener = new ButtonListener();
        for (int i = 0; i < 24; i++) {
            buttons.add((Button) buttonViews[i]);
            buttons.get(i).setOnClickListener(buttonListener);
        }
        stream.add("0");
        display();
    }

    /**
     * Handles button click operations
     */
    class ButtonListener implements View.OnClickListener {
        private String lastKey = "_";
        private String nextKey = "_";

        @Override
        public void onClick(View v) {
            Button button = (Button) findViewById(v.getId());
            lastKey = nextKey;
            nextKey = button.getText().toString();
            Log.v("MainActivity", "" + stream.toString());
            if (numbers.contains(nextKey)) {
                if (lastKey.equals("="))
                    stream.clear();
                pushNumber(nextKey);
            } else if (functions.contains(nextKey)) {
                if (!stream.isEmpty() && !functions.contains(lastKey))
                    pushFunction(nextKey);
            } else if (nextKey.equals("=")) {
                functionize();
            } else if (nextKey.equals("±")) {
                Pkg lastNumber = getNumber(stream.size() - 1);
                double newNumber = -1 * Double.parseDouble(lastNumber.str);
                clear();
                stream.add(Double.toString(newNumber));
                display();
            } else if (nextKey.equals("⌫")) {
                stream.remove(stream.size() - 1);
                if (stream.isEmpty())
                    stream.add("0");
                display();
            } else if (nextKey.equals("CE")) {
                stream.clear();
                textBox.setText("0");
                display();
            } else if (nextKey.equals("C")) {
                clear();
//                stream.add("0");
                display();
//                TODO: make it return number1(1 + (number2/100)   if there is a last number
            } else if (nextKey.equals("%")) {
                Pkg lastNumber = getNumber(stream.size() - 1);
                if (!lastNumber.str.equals("")) {
                    double newNumber = Double.parseDouble(lastNumber.str) / 100;
                    clear();
                    stream.add(Double.toString(newNumber));
                    display();
                }
            } else if (nextKey.equals("x²")) {
                Pkg lastNumber = getNumber(stream.size() - 1);
                if (!(lastNumber.str.equals("") || lastNumber.str.equals("0"))) {
                    double newNumber = Double.parseDouble(lastNumber.str) * Double.parseDouble(lastNumber.str);
                    clear();
                    stream.add(Double.toString(newNumber));
                    display();
                }
            } else if (nextKey.equals("1/x")) {
                Pkg lastNumber = getNumber(stream.size() - 1);
                if (!lastNumber.str.equals("")) {
                    double newNumber = 1 / Double.parseDouble(lastNumber.str);
                    clear();
                    stream.add(Double.toString(newNumber));
                    display();
                }
            } else if (nextKey.equals("√")) {
                Pkg lastNumberPkg = getNumber(stream.size() - 1);
                if (!lastNumberPkg.str.equals("")) {
                    Double newNumber = Double.parseDouble(lastNumberPkg.str);
                    if (newNumber > 0.0) {
                        newNumber = Math.sqrt(newNumber);
                        clear();
                        stream.add(Double.toString(newNumber));
                        display();
                    }
                }
            }
        }
    }


    /**
     * Does the grunt work in mapping out the buttons into arrays
     */
    private void setUpViewArray() {
        buttonViews[0] = findViewById(R.id.button_0);
        buttonViews[1] = findViewById(R.id.button_1);
        buttonViews[2] = findViewById(R.id.button_2);
        buttonViews[3] = findViewById(R.id.button_3);
        buttonViews[4] = findViewById(R.id.button_4);
        buttonViews[5] = findViewById(R.id.button_5);
        buttonViews[6] = findViewById(R.id.button_6);
        buttonViews[7] = findViewById(R.id.button_7);
        buttonViews[8] = findViewById(R.id.button_8);
        buttonViews[9] = findViewById(R.id.button_9);
        buttonViews[10] = findViewById(R.id.button_10);
        buttonViews[11] = findViewById(R.id.button_11);
        buttonViews[12] = findViewById(R.id.button_12);
        buttonViews[13] = findViewById(R.id.button_13);
        buttonViews[14] = findViewById(R.id.button_14);
        buttonViews[15] = findViewById(R.id.button_15);
        buttonViews[16] = findViewById(R.id.button_16);
        buttonViews[17] = findViewById(R.id.button_17);
        buttonViews[18] = findViewById(R.id.button_18);
        buttonViews[19] = findViewById(R.id.button_19);
        buttonViews[20] = findViewById(R.id.button_20);
        buttonViews[21] = findViewById(R.id.button_21);
        buttonViews[22] = findViewById(R.id.button_22);
        buttonViews[23] = findViewById(R.id.button_23);
    }

    /**
     * adds number to stream and updates display
     * makes sure there's only one decimal point
     *
     * @param num
     */
    private void pushNumber(String num) {
        if (num.equals(".")) {
            if (getNumber(stream.size() - 1).str.contains(".")) {
                System.out.println("double point error");
            } else if (stream.isEmpty() || functions.contains(stream.get(stream.size() - 1))) {
                stream.add("0");
                stream.add(num);
            } else {
                stream.add(num);
            }

        } else {
            stream.add(num);
        }
        display();
    }

    /**
     * adds function to stream and calulates things accordingly. I mean, eventually XD
     *
     * @param func
     */
    private void pushFunction(String func) {
        if (previousFunc(stream.size() - 1) >= 0) {
            Pkg lastN = getNumber(stream.size() - 1);
            Pkg prevN = getNumber(lastN.integer - 1);
            if (!(lastN.str.equals("") || prevN.str.equals(""))) {
                double solution = determineFunction(Double.parseDouble(lastN.str), Double.parseDouble(prevN.str), stream.get(lastN.integer));
                stream.clear();
                stream.add(Double.toString(solution));
                display();
            }
        }
        stream.add(func);
    }

    /**
     * turns the last two numbers contained in the stream into mathematical constructs and applies the appropriate operation on them
     */
    private void functionize() {
        if (previousFunc(stream.size() - 1) >= 0) {
            Pkg lastN = getNumber(stream.size() - 1);
            Pkg prevN = getNumber(lastN.integer - 1);
            if (!(lastN.equals("") || prevN.equals(""))) {
                double solution = determineFunction(Double.parseDouble(prevN.str), Double.parseDouble(lastN.str), stream.get(lastN.integer));
                stream.clear();
                stream.add(Double.toString(solution));
                display();
            }
        }
    }

    /**
     * applies an operation to two numbers, given two numbers and a function sign in a String
     *
     * @param num1
     * @param num2
     * @param symbol
     * @return
     */
    private double determineFunction(double num1, double num2, String symbol) {
        switch (symbol) {
            case ("+"):
                return num1 + num2;
            case ("-"):
                return num1 - num2;
            case ("x"):
//            case("*"):
                return num1 * num2;
//            case("/"):
            case ("÷"):
                return num1 / num2;
            default:
                return 0.0;
        }
    }

    /**
     * returns int location of last function in stream, else it returns -1
     *
     * @param start
     * @return location in stream of function symbol
     */
    int previousFunc(int start) {
        for (int i = start; i > 0; i--) {
            if (functions.contains(stream.get(i)))
                return i;
        }
        return -1;
    }

    /**
     * updates outputBox
     */
    private void display() {
        if (stream.isEmpty()) {
            textBox.setText("0");
        } else {
            Pkg nPkg = getNumber(stream.size() - 1);
            if (!nPkg.str.equals("")) {
                if (nPkg.str.contains("Infinity"))
                    nPkg.str = "Infinity";
                double number = Double.parseDouble(nPkg.str);
                String lastKey = stream.get(stream.size() - 1);
//            filters ".0" from whole numbers using int parsing, unless last digit is . or 0 because both those could
//            be typed intentionally and would still equal whole numbers
                if (number % 1 == 0 && !(lastKey.equals(".") || lastKey.equals("0"))) {
                    long longNumber = (long) number;
                    if (longNumber < Long.MAX_VALUE) {
                        textBox.setText(formatString(Long.toString(longNumber)));
                    }
                } else {
                    if (number < Double.MAX_VALUE)
                        textBox.setText(formatString(nPkg.str));
                }
            } else {
                textBox.setText("0");
            }
        }
    }

    /**
     * turns digits in the stream into numbers
     *
     * @param start as int; searches backwards from start
     * @return Pkg object containing number as string, and integer where the first digit of the number was found
     */
    private Pkg getNumber(int start) {
        if (start >= stream.size())
            throw new IndexOutOfBoundsException("start must be smaller than stream.size start =" + start + " stream.size=" + stream.size());
        int i = start;
        StringBuilder number = new StringBuilder();
        while (i >= 0 && !functions.contains(stream.get(i))) {
            number.insert(0, stream.get(i));
            i--;
        }
        return new Pkg(number.toString(), i);
    }

    /**
     * removes the last number from the stream
     */
    private void clear() {
        Pkg lastNum = getNumber(stream.size() - 1);
        while (lastNum.integer + 1 < stream.size()) {
            stream.remove(lastNum.integer + 1);
        }
        display();
    }

    /**
     * formats number string to limit character length
     *
     * @param inputStr
     * @return
     */
    // TODO: needs reworking with better numbers
    private String formatString(String inputStr) {
        /*if (inputStr.length() > 20)
            textBox.setTextSize(24);
        else if (inputStr.length() > 14)
            textBox.setTextSize(36);
        else if (inputStr.length() > 10)
            textBox.setTextSize(48);
        else
            textBox.setTextSize(56);*/
        return inputStr;
//        return null;
    }
}
