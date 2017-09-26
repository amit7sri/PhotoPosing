package photoposing.com.amko0l.photoposing;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.vision.v1.model.LatLng;
import com.google.api.services.vision.v1.model.LocationInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    private static final String CLOUD_VISION_API_KEY = "AIzaSyCBj7r40J8AyQHak9QmIt3oGKZsS8gzN4g";
    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";

    private static final String TAG = "Amit" + MainActivity.class.getSimpleName();
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;

    private TextView mImageDetails;
    private ImageView mMainImage;
    private ProgressBar mProgressBar;
    FloatingActionButton mfab;

    //FireBase
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    DatabaseReference databaseReference;
    Uri mUri;

    //ImageView
    ImageView fbimage1;
    ImageView fbimage2;
    ImageView fbimage3;
    ImageView fbimage4;

    ImageView gimage1;
    ImageView gimage2;
    ImageView gimage3;
    ImageView gimage4;

    TextView smiletext;
    Button launchmapbutton;
    List<LandMarks> landMarksList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fbimage1 = (ImageView) findViewById(R.id.fbimage1);
        fbimage2 = (ImageView) findViewById(R.id.fbimage2);
        fbimage3 = (ImageView) findViewById(R.id.fbimage3);
        fbimage4 = (ImageView) findViewById(R.id.fbimage4);

        gimage1 = (ImageView) findViewById(R.id.gimage1);
        gimage2 = (ImageView) findViewById(R.id.gimage2);
        gimage3 = (ImageView) findViewById(R.id.gimage3);
        gimage4 = (ImageView) findViewById(R.id.gimage4);

        launchmapbutton = (Button) findViewById(R.id.launch_map);
        smiletext = (TextView) findViewById(R.id.smile_image);
        smiletext.setVisibility(View.GONE);
        launchmapbutton.setVisibility(View.GONE);


        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mfab = (FloatingActionButton) findViewById(R.id.fab);
        mfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder
                        .setMessage(R.string.dialog_select_prompt)
                        .setPositiveButton(R.string.dialog_select_gallery, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startGalleryChooser();
                                setInvisible();
                            }
                        })
                        .setNegativeButton(R.string.dialog_select_camera, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startCamera();
                                setInvisible();
                            }
                        });
                builder.create().show();
            }
        });

        launchmapbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                intent.putExtra("latitude",lati_longi.getLatitude());
                intent.putExtra("longitude",lati_longi.getLongitude());
                startActivity(intent);
                //launch map
            }
        });

        mImageDetails = (TextView) findViewById(R.id.image_details);
        mMainImage = (ImageView) findViewById(R.id.main_image);

        landMarksList = new ArrayList<>();
    }


    public void startGalleryChooser() {
        if (PermissionUtils.requestPermission(this, GALLERY_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                    GALLERY_IMAGE_REQUEST);
        }
    }

    public void startCamera() {
        if (PermissionUtils.requestPermission(
                this,
                CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    public File getCameraFile() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            uploadImage(data.getData());
        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            uploadImage(photoUri);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
                    startCamera();
                }
                break;
            case GALLERY_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, GALLERY_PERMISSIONS_REQUEST, grantResults)) {
                    startGalleryChooser();
                }
                break;
        }
    }

    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                mProgressBar.setVisibility(View.VISIBLE);
                mfab.setVisibility(View.INVISIBLE);
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                400);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream);

                mMainImage.setImageBitmap(bitmap);
                mUri = uri;
                detectVisionCloudVision(bitmap);
            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                mProgressBar.setVisibility(View.GONE);
                mfab.setVisibility(View.VISIBLE);
                Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            mProgressBar.setVisibility(View.GONE);
            mfab.setVisibility(View.VISIBLE);
            Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }

    private void detectVisionCloudVision(final Bitmap bitmap) throws IOException {
        // Switch text to loading
        mImageDetails.setText(R.string.loading_message);
        // Do the real work in an async task, because we need to use the network anyway
        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    VisionRequestInitializer requestInitializer =
                            new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                                /**
                                 * We override this so we can inject important identifying fields into the HTTP
                                 * headers. This enables use of a restricted cloud platform API key.
                                 */
                                @Override
                                protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                                        throws IOException {
                                    super.initializeVisionRequest(visionRequest);
                                    String packageName = getPackageName();
                                    visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                                    String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                                    visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                                }
                            };

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(requestInitializer);

                    Vision vision = builder.build();

                    List<Feature> featureList = new ArrayList<>();
                    Feature landmarkDetection = new Feature();
                    landmarkDetection.setType("LANDMARK_DETECTION");
                    landmarkDetection.setMaxResults(1);
                    featureList.add(landmarkDetection);

                    /*Feature webdetection = new Feature();
                    landmarkDetection.setType("WEB_DETECTION");
                    landmarkDetection.setMaxResults(10);
                    featureList.add(landmarkDetection);*/

                    List<AnnotateImageRequest> imageList = new ArrayList<>();
                    AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();
                    Image base64EncodedImage = getBase64EncodedJpeg(bitmap);
                    annotateImageRequest.setImage(base64EncodedImage);
                    annotateImageRequest.setFeatures(featureList);
                    imageList.add(annotateImageRequest);

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(imageList);

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    annotateRequest.setDisableGZipContent(true);
                    Log.d(TAG, "sending request");

                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return convertResponseToString(response);
                } catch (GoogleJsonResponseException e) {
                    Log.d(TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "failed to make API request because of other IOException " +
                            e.getMessage());
                }
                return "Cloud Vision API request failed. Check logs for details.";
            }
            protected void onPostExecute(String result) {
                //mImageDetails.setText(result);
                if(result.contains("nothing")){
                    Toast.makeText(MainActivity.this, "No LandMark Detected by Google vision API", Toast.LENGTH_SHORT).show();
                }
                mImageDetails.setVisibility(View.GONE);
                mfab.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                smiletext.setVisibility(View.VISIBLE);
            }
        }.execute();
    }

    public Image getBase64EncodedJpeg(Bitmap bitmap) {
        Image image = new Image();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        image.encodeContent(imageBytes);
        return image;
    }

    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    LatLng lati_longi;
    private String convertResponseToString(BatchAnnotateImagesResponse response) {
        Log.d(TAG, "BatchAnnotateImagesResponse " + response);
        StringBuilder message = new StringBuilder("Results:\n\n");

        message.append("Landmarks:\n");
        final List<EntityAnnotation> landmarks = response.getResponses().get(0).getLandmarkAnnotations();
        if (landmarks != null) {
            for (final EntityAnnotation landmark : landmarks) {
                message.append(String.format(Locale.getDefault(), "%s", landmark.getLocations()));

                List<LocationInfo> latlng = landmark.getLocations();
                if(latlng != null && latlng.size()>0){
                    lati_longi = latlng.get(0).getLatLng();
                }

                Log.d(TAG, "Landmark is from response " + landmark.getDescription());

                databaseReference = FirebaseDatabase.getInstance().getReference(landmark.getDescription().toString());
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        landMarksList.clear();
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            LandMarks landMarkschild = child.getValue(LandMarks.class);
                            Log.d(TAG,"landMarkschild  onDataChange");
                            landMarksList.add(landMarkschild);
                        }

                        if(landMarksList.size()==1){
                            fbimage1.setVisibility(View.VISIBLE);
                            Picasso.with(MainActivity.this).load(landMarksList.get(0).getUrl()).fit().into(fbimage1);
                        }else if(landMarksList.size()==2){
                            fbimage1.setVisibility(View.VISIBLE);
                            fbimage2.setVisibility(View.VISIBLE);
                            Picasso.with(MainActivity.this).load(landMarksList.get(0).getUrl()).fit().into(fbimage1);
                            Picasso.with(MainActivity.this).load(landMarksList.get(1).getUrl()).fit().into(fbimage2);
                        }else if(landMarksList.size()==3){
                            fbimage1.setVisibility(View.VISIBLE);
                            fbimage2.setVisibility(View.VISIBLE);
                            fbimage3.setVisibility(View.VISIBLE);
                            Picasso.with(MainActivity.this).load(landMarksList.get(0).getUrl()).fit().into(fbimage1);
                            Picasso.with(MainActivity.this).load(landMarksList.get(1).getUrl()).fit().into(fbimage2);
                            Picasso.with(MainActivity.this).load(landMarksList.get(2).getUrl()).fit().into(fbimage3);
                        }else if(landMarksList.size()>3){
                            fbimage1.setVisibility(View.VISIBLE);
                            fbimage2.setVisibility(View.VISIBLE);
                            fbimage3.setVisibility(View.VISIBLE);
                            fbimage4.setVisibility(View.VISIBLE);
                            Picasso.with(MainActivity.this).load(landMarksList.get(0).getUrl()).fit().into(fbimage1);
                            Picasso.with(MainActivity.this).load(landMarksList.get(1).getUrl()).fit().into(fbimage2);
                            Picasso.with(MainActivity.this).load(landMarksList.get(2).getUrl()).fit().into(fbimage3);
                            Picasso.with(MainActivity.this).load(landMarksList.get(3).getUrl()).fit().into(fbimage4);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(MainActivity.this, "DatabaseError error occured, pls try again ", Toast.LENGTH_LONG).show();
                    }
                });

                final String id = databaseReference.push().getKey();
                //new EndpointsAsyncTask().execute(new Pair<Context, String>(this, landmark.getDescription()));
                //Firebase
                String path = landmark.getDescription()+ "/" + UUID.randomUUID() +".png";
                StorageReference fbsref = storage.getReference(path);

                UploadTask uploadTask = fbsref.putFile(mUri);
                uploadTask.addOnSuccessListener(MainActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUri = taskSnapshot.getDownloadUrl();
                        //String urlencode = URLEncoder.encode(downloadUri.toString());

                        new ServletPostAsyncTask().execute(new Pair<Context, String>(MainActivity.this,
                                (downloadUri.toString())));

                        Log.d("Amit" , "download uri is  " + downloadUri.toString());
                        LandMarks landmark = new LandMarks(downloadUri.toString(), String.valueOf(lati_longi.getLatitude()), String.valueOf(lati_longi.getLongitude()));
                        databaseReference.child(id).setValue(landmark);
                        Toast.makeText(MainActivity.this, "Photo Added", Toast.LENGTH_LONG).show();
                        mfab.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
                    }

                });
                //message.append("\n");
            }
        } else {
            message.append("nothing\n");
           //Toast.makeText(MainActivity.this, "Landmark not found", Toast.LENGTH_LONG).show();
        }
        return message.toString();
    }

    class ServletPostAsyncTask extends AsyncTask<Pair<Context, String>, Void, String> {
        private Context context;

        @Override
        protected String doInBackground(Pair<Context, String>... params) {
            context = params[0].first;
            String name = params[0].second;

            try {
                // Set up the request
                URL url = new URL("http://photoposing-165504.appspot.com/hello");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);

                // Build name data request params
                Map<String, String> nameValuePairs = new HashMap<>();
                nameValuePairs.put("name", name);
                String postParams = buildPostDataString(nameValuePairs);

                // Execute HTTP Post
                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(postParams);
                writer.flush();
                writer.close();
                outputStream.close();
                connection.connect();

                // Read response
                int responseCode = connection.getResponseCode();
                StringBuilder response = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    Log.d(TAG,  "responsecode OK " + response.toString());
                    return response.toString();
                }
                return "Error: " + responseCode + " " + connection.getResponseMessage();

            } catch (IOException e) {
                return e.getMessage();
            }
        }

        private String buildPostDataString(Map<String, String> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first) {
                    first = false;
                } else {
                    result.append("&");
                }

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG,  "response from servelet " + result);
            //Toast.makeText(context, result, Toast.LENGTH_LONG).show();
            //Log.d(TAG, "listener is null");
            launchmapbutton.setVisibility(View.VISIBLE);
            smiletext.setVisibility(View.VISIBLE);

            result.replaceAll("\\s","");
            String[] response = result.split(",");

            String facedetectionresp ="";
            String[] googleimage = new String[4];
            int j=0;
            for(int i=0 ; i<response.length ;i++){
                response[i].replaceAll("\\s","");
                Log.d(TAG, "response from servlet abc  " + response[i]);
                if(response[i].contains("detectionConfidence") || response[i].contains("joyLikelihood") ||
                        response[i].contains("sorrowLikelihood") || response[i].contains("angerLikelihood") || response[i].contains("surpriseLikelihood")) {
                    if(!response[i].contains("UNLIKELY"))
                        facedetectionresp += response[i];
                }else if(response[i].contains("similarimageURL")){
                    if(j<4) {
                        Log.d(TAG, "urls image url response " + response[i]);
                        String[] urls = response[i].split("amko0lsimilarimageURL:");
                        Log.d(TAG,  "urls image url after split  " + urls[0] +"  "+urls[1]);

                        googleimage[j] = urls[1];
                        j++;
                    }
                }
            }
            Log.d(TAG,  "face result " + facedetectionresp);
            if(facedetectionresp==null || facedetectionresp.length()==0)
                smiletext.setText("Face detection gives no result");
            else
                smiletext.setText(facedetectionresp);

            for(int i=0;i<googleimage.length;i++){
                Log.d(TAG,  "google image url " + googleimage[i]);
            }

            if(googleimage.length==1){
                gimage1.setVisibility(View.VISIBLE);
                Picasso.with(MainActivity.this).load(googleimage[0]).fit().into(gimage1);
            }else if(googleimage.length==2){
                gimage1.setVisibility(View.VISIBLE);
                gimage2.setVisibility(View.VISIBLE);
                Picasso.with(MainActivity.this).load(googleimage[1]).fit().into(gimage1);
                Picasso.with(MainActivity.this).load(googleimage[0]).fit().into(gimage2);
            }else if(googleimage.length==3){
                gimage1.setVisibility(View.VISIBLE);
                gimage2.setVisibility(View.VISIBLE);
                gimage3.setVisibility(View.VISIBLE);
                Picasso.with(MainActivity.this).load(googleimage[2]).fit().into(gimage1);
                Picasso.with(MainActivity.this).load(googleimage[1]).fit().into(gimage2);
                Picasso.with(MainActivity.this).load(googleimage[0]).fit().into(gimage3);
            }else if(googleimage.length>3){
                gimage1.setVisibility(View.VISIBLE);
                gimage2.setVisibility(View.VISIBLE);
                gimage3.setVisibility(View.VISIBLE);
                gimage4.setVisibility(View.VISIBLE);
                Picasso.with(MainActivity.this).load(googleimage[3]).fit().into(gimage1);
                Picasso.with(MainActivity.this).load(googleimage[2]).fit().into(gimage2);
                Picasso.with(MainActivity.this).load(googleimage[1]).fit().into(gimage3);
                Picasso.with(MainActivity.this).load(googleimage[0]).fit().into(gimage4);
            }
        }
    }

    void setInvisible(){
        fbimage1.setVisibility(View.GONE);
        fbimage2.setVisibility(View.GONE);
        fbimage3.setVisibility(View.GONE);
        fbimage4.setVisibility(View.GONE);
        gimage1.setVisibility(View.GONE);
        gimage2.setVisibility(View.GONE);
        gimage3.setVisibility(View.GONE);
        gimage4.setVisibility(View.GONE);
        smiletext.setVisibility(View.GONE);
        launchmapbutton.setVisibility(View.GONE);
    }
}
