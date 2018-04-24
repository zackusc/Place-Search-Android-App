package com.example.lizi.place_search;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class PhotosFragment extends Fragment{

    private String placeId;
    protected GeoDataClient mGeoDataClient;
    TextView mTextView;
    private Bitmap[] bitmaps;
    private int photoNum;

    PlacePhotoMetadataBuffer photoMetadataBuffer;

    private int check = 0;

    ImageView sampleImage;

    public PhotosFragment() {
    }

    public static PhotosFragment newInstance(String id) {
        PhotosFragment fragment = new PhotosFragment();
        Bundle args = new Bundle();
        args.putString("place_id", id);
        fragment.setArguments(args);
        return fragment;
    }




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photos, container, false);
        placeId = getArguments().getString("place_id");
        Log.d("photos", "onCreate placeId: " + placeId);

//        mTextView = rootView.findViewById(R.id.textView2);
        sampleImage = rootView.findViewById(R.id.imageView2);

        mGeoDataClient = Places.getGeoDataClient(getActivity());

        getPhotos();

        return rootView;
    }


    private void getPhotos() {
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);

        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                // Get the list of photos.
                PlacePhotoMetadataResponse photos = task.getResult();
                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                photoMetadataBuffer = photos.getPhotoMetadata();
                photoNum = photoMetadataBuffer.getCount();

                Log.d("photos", "number of photos: " + photoNum);

                bitmaps = new Bitmap[photoNum + 1];

                Log.d("photos", "bitmaps length" + bitmaps.length);

                for (int i = 0; i < photoNum; i++) {
                    getPhoto(i);
                }
            }
        });
    }

    private void getPhoto(int num) {
        PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(num);
        CharSequence attribution = photoMetadata.getAttributions();
        final int n = num;
        // Get a full-size bitmap for the photo.
        Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
        photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                PlacePhotoResponse photo = task.getResult();
                Bitmap bitmap = photo.getBitmap();
                bitmaps[n] = bitmap;
                check++;
                if(n == 4) {
                    sampleImage.setImageBitmap(bitmaps[n]);
                }
                if(check == photoNum) {
                    photoMetadataBuffer.release();
                }
            }
        });
    }


}
