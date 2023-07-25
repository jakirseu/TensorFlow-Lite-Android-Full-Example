package com.example.tflite;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {

    Interpreter tflite;
    EditText input;
    TextView output;
    Button predict;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        predict =findViewById(R.id.btn_predict);
        output =findViewById(R.id.tv_output);
        input =findViewById(R.id.et_input);

        try {
            tflite = new Interpreter(loadModelFile());
        }catch (Exception ex){
            ex.printStackTrace();
        }
        predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float prediction= doPredict(input.getText().toString());
                System.out.println(prediction);
                output.setText(Float.toString(prediction));
            }
        });

    }
    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor=this.getAssets().openFd("output_model.tflite");
        FileInputStream inputStream=new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel=inputStream.getChannel();
        long startOffset=fileDescriptor.getStartOffset();
        long declareLength=fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,declareLength);
    }
    private float doPredict(String inputString) {
        float[] inputVal=new float[1];
        inputVal[0]=Float.parseFloat(inputString);
        float[][] output=new float[1][1];
        tflite.run(inputVal,output);
        return output[0][0];
    }


}