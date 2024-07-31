package com.choiminseon.fletterapp;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.choiminseon.fletterapp.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TopPredictFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TopPredictFragment extends Fragment {

    // 분류 결과와 신뢰도 표시
    TextView result, confidence;
    // 선택된 이미지를 표시
    ImageView imageView;
    // 이미지를 선택할 버튼
    Button btnPredict;
    // 이미지 크기
    int imageSize = 224;

    public TopPredictFragment() {
        // Required empty public constructor
    }

    public static TopPredictFragment newInstance(String param1, String param2) {
        TopPredictFragment fragment = new TopPredictFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // 매개변수 초기화
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_top_predict_recommend, container, false);

        result = view.findViewById(R.id.result);
        confidence = view.findViewById(R.id.confidence);
        imageView = view.findViewById(R.id.imageView);
        btnPredict = view.findViewById(R.id.btnPredict);

        btnPredict.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                // 권한이 있는 경우 카메라 실행
                if (requireActivity().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 1);
                } else {
                    // 카메라 권한이 없으면 요청
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });

        return view;
    }

    public void classifyImage(Bitmap image){
        try {
            Model model = Model.newInstance(requireActivity().getApplicationContext());

            // 입력 데이터 생성
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, imageSize, imageSize, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            // 224 * 224 픽셀 이미지 배열 생성
            int [] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

            // 픽셀 데이터를 바이트 버퍼에 추가
            int pixel = 0;
            for(int i = 0; i < imageSize; i++){
                for(int j = 0; j < imageSize; j++){
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // 모델 추론을 실행 및 결과 가져오기
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            // 가장 높은 신뢰도의 클래스를 찾는다.
            int maxPos = 0;
            float maxConfidence = 0;
            for(int i = 0; i < confidences.length; i++){
                if(confidences[i] > maxConfidence){
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }

            // 클래스 이름
            String[] classes = {"튤립", "장미", "데이지"};
            result.setText(classes[maxPos]);

            // 각 클래스의 신뢰도를 문자열로 생성
            StringBuilder s = new StringBuilder();
            for(int i = 0; i < classes.length; i++){
                s.append(String.format("%s: %.1f%%\n", classes[i], confidences[i] * 100));
            }
            confidence.setText(s.toString());

            // 모델 리소스 해제
            model.close();
        } catch (IOException e) {
            // 예외 처리
            e.printStackTrace();
        }
    }

    // 활동 결과를 처리하는 메서드
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            imageView.setImageBitmap(image);

            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            classifyImage(image);
        }
    }
}
