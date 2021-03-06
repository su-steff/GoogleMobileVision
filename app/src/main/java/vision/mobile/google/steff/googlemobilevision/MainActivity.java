package vision.mobile.google.steff.googlemobilevision;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_BARCODE_CAPTURE = 2;
    private static final int REQUEST_TEXT_CAPTURE = 3;
    private static final String TAG = "MainActivity";

    private Button buttonCaptureImage;
    private FaceView faceDetectImageView;

    private Button buttonCaptureBarcode;
    private ImageView imageViewBarcode;

    private Button buttonCaptureText;
    private ImageView imageViewText;

    private String pictureImagePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setViews();
        setListeners();
    }

    private void setViews() {
        //CardView FaceDetection
        buttonCaptureImage = (Button) findViewById(R.id.btnTakePicture);
        faceDetectImageView = (FaceView) findViewById(R.id.imageViewShowImage);
        //CardView BarcodeDetection
        buttonCaptureBarcode = (Button) findViewById(R.id.btnTakeBarcode);
        imageViewBarcode = (ImageView) findViewById(R.id.imageViewShowBarcode);
        //CardView TextDetection
        buttonCaptureText = (Button) findViewById(R.id.btnTakeTextImage);
        imageViewText = (ImageView) findViewById(R.id.imageViewShowText);
    }

    private void setListeners() {
        buttonCaptureImage.setOnClickListener(this);
        faceDetectImageView.setOnClickListener(this);
        buttonCaptureBarcode.setOnClickListener(this);
        imageViewBarcode.setOnClickListener(this);
        buttonCaptureText.setOnClickListener(this);
        imageViewText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnTakePicture: {
                openBackCamera(REQUEST_IMAGE_CAPTURE);
                break;
            }
            case R.id.imageViewShowImage: {
                if (faceDetectImageView.getBackground() != null) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    Log.e(TAG, "Current_Photo_Path: " + pictureImagePath);
                    Uri imgUri = Uri.parse(pictureImagePath);
                    intent.setDataAndType(imgUri, "image/*");
                    startActivity(intent);
                }
                break;
            }
            case R.id.btnTakeBarcode: {
                openBackCamera(REQUEST_BARCODE_CAPTURE);
                break;
            }
            case R.id.btnTakeTextImage: {
                openBackCamera(REQUEST_TEXT_CAPTURE);
                break;
            }
        }
    }

    private void openBackCamera(int requestCode) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        pictureImagePath = storageDir.getAbsolutePath() + "/" + imageFileName;
        File file = new File(pictureImagePath);
        Uri outputFileUri = Uri.fromFile(file);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            File imgFile = new File(pictureImagePath);
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                //ImageView myImage = (ImageView) findViewById(R.id.imageViewShowImage);
                //myImage.setImageBitmap(myBitmap);
                // }
                getSparseFaces(myBitmap);
                //setPic();
            }
        } else if (requestCode == REQUEST_BARCODE_CAPTURE) {
            Log.e(TAG, "CAPTURE_BARCODE");
            File imgFile = new File(pictureImagePath);
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                myBitmap = BitmapFactory.decodeResource(
                        getApplicationContext().getResources(),
                        R.drawable.barcode_image);
                imageViewBarcode.setImageBitmap(myBitmap);
                BarcodeDetector detector =
                        new BarcodeDetector.Builder(getApplicationContext())
                                .setBarcodeFormats(Barcode.ALL_FORMATS)
                                .build();
                if (!detector.isOperational()) {
                    Log.e(TAG, "Could not set up the detector!");
                    return;
                } else {
                    Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
                    SparseArray<Barcode> barcodes = detector.detect(frame);
                    Log.e(TAG, "BARCODE_STRING :" + barcodes.toString());
                    if (barcodes.size() != 0) {
                        Barcode thisCode = barcodes.valueAt(0);
                        Log.e(TAG, "BARCODE_FOUND :" + thisCode.rawValue);
                    }
                }
                // }
//                getSparseFaces(myBitmap);
                //setPic();
//            }
            } else if (requestCode == REQUEST_TEXT_CAPTURE) {
                Log.e(TAG, "CAPTURE_TEXT");
//            File imgFile = new File(pictureImagePath);
//            if (imgFile.exists()) {
//                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                //ImageView myImage = (ImageView) findViewById(R.id.imageViewShowImage);
                //myImage.setImageBitmap(myBitmap);
                // }
//                getSparseFaces(myBitmap);
                //setPic();
//            }
            }
        }
    }

    private void getSparseFaces(Bitmap bitmap) {
        //Bitmap myBitmap = BitmapFactory.decodeStream(stream);
        FaceDetector detector = new FaceDetector.Builder(getApplicationContext())
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Face> faces = detector.detect(frame);

        if (!detector.isOperational()) {
            // Note: The first time that an app using face API is installed on a device, GMS will
            // download a native library to the device in order to do detection.  Usually this
            // completes before the app is run for the first time.  But if that download has not yet
            // completed, then the above call will not detect any faces.
            //
            // isOperational() can be used to check if the required native library is currently
            // available.  The detector will automatically become operational once the library
            // download completes on device.
            Log.e(TAG, "Face detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(TAG, getString(R.string.low_storage_error));
            }
        }

        FaceView overlay = (FaceView) findViewById(R.id.imageViewShowImage);
        overlay.setContent(bitmap, faces);

        // Although detector may be used multiple times for different images, it should be released
        // when it is no longer needed in order to free native resources.
        detector.release();

    }

    /*
    private void setPic() {
        int targetW = faceDetectImageView.getWidth();
        int targetH = faceDetectImageView.getHeight();
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pictureImagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(pictureImagePath, bmOptions);
        faceDetectImageView.setImageBitmap(bitmap);
    }
    */
}
