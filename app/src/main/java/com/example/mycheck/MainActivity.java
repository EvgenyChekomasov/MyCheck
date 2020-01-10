package com.example.mycheck;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    int voltage = 220;
    String material = "Al";
    //String dist;
    double distance;
    //String pow;
    double power;
    //String cosFi;
    double cosf = 1;
    double current = 0;
    double dU = 0;
    Cable cable = new Cable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    }

    // Вывод результатов расчета тока КЗ
    public String calcResult1() {
        String currentCir = String.format("%.3f", current);
        return currentCir;
    }

    // Вывод результатов расчета потери напряжения
    public String calcResult2() {
        String deltaU = String.format("%.3f", dU);
        return deltaU;
    }

    public void runCalc(View view) {

        // Выбор и задание напряжения
        Switch swVolt = (Switch) findViewById(R.id.switchVolt);
        swVolt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    voltage = 380;
                } else {
                    voltage = 220;
                }
            }
        });

        //Выбор материала кабеля
        Switch swMat = (Switch) findViewById(R.id.switchMat);
        swMat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    material = "Cu";
                } else {
                    material = "Al";
                }
            }
        });
        //Звдвние материала кабеля
        cable.setMaterial(material);

        //Выбор и задание сечения кабеля
        Spinner area = (Spinner) findViewById(R.id.profile);
        String profile = String.valueOf(area.getSelectedItem());
        cable.setProfile(profile);

        // Ввод длины кабеля в км
        EditText inputLength = (EditText) findViewById(R.id.inputLength);
        distance = Double.valueOf(inputLength.getText().toString());

        // Указание мощности
        EditText inputPower = (EditText) findViewById(R.id.inputPower);
        power = Double.valueOf(inputPower.getText().toString());

        //Указание коэффициента мощности
        EditText cosinus = (EditText) findViewById(R.id.cosinus);
        cosf = Double.valueOf(cosinus.getText().toString());

        //Определение величин удельного сопротивления кабеля
        cable.choiceResistance();

        if (voltage == 220) {
            current = voltage / (cable.getzLoop() * distance);
            dU = power * cable.getzLoop() * distance * 100 / Math.pow(voltage, 2);
        } else {
            current = voltage / (Math.sqrt(3) * Math.sqrt(Math.pow(cable.getrActiv(), 2) + Math.pow(cable.getxReactive(), 2)) * distance);
            dU = (power * cable.getrActiv() + power * Math.tan(Math.acos(cosf)) * cable.getxReactive()) * distance * 100 / (Math.pow(voltage, 2) * cosf);
        }

        /*String str = cable.getrActiv() + " " + cable.getxReactive() + " " + cable.getzLoop() + "\n" + voltage + " " + distance + " " + power +
                "\n" + current + " " + dU;
        TextView prom = (TextView) findViewById(R.id.prom);
        prom.setText(str);*/


        String currentCir = String.format("%.3f", current);
        String deltaU = String.format("%.3f", dU);
        String output = "Для кабеля с " + material + " жилой, длиной " + distance + " м, сечением " + profile + "\n" + "\n" +
                "ток короткого замыкания составляет " + currentCir + " кА на напряжении " + voltage + " В." + "\n" + "\n" +
                "При нагрузке " + power + " кВт потери напряжения составляют " + deltaU + " %";

        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("output", output);
        startActivity(intent);

    }
}