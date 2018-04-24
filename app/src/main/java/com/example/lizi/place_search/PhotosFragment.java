package com.example.lizi.place_search;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.Arrays;

public class PhotosFragment extends Fragment{

    private String placeId;
    protected GeoDataClient mGeoDataClient;
    TextView mTextView;
    private Bitmap[] bitmaps;
    private int photoNum;

    RecyclerView photosRecyclerView;
    PlacePhotoMetadataBuffer photoMetadataBuffer;
    private PhotoAdapter photoAdapter;

    private int check;

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
//        sampleImage = rootView.findViewById(R.id.imageView2);

        mGeoDataClient = Places.getGeoDataClient(getActivity());

        photosRecyclerView = rootView.findViewById(R.id.photos_recyclerView);
        photosRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        photoAdapter = new PhotoAdapter(new ArrayList<Bitmap>());
        photosRecyclerView.setAdapter(photoAdapter);
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

                bitmaps = new Bitmap[photoNum];
                check = 0;

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

                Log.d("photos", "no." + n + " photo is downloaded!");
                Log.d("photos", "check=" + check);

                if(check == photoNum) {

                    boolean allDownloaded = true;



                    ArrayList<Bitmap> photos = new ArrayList<>(Arrays.asList(bitmaps));
//                    Log.d("photos", "arraylist size: " + photos.size());

                    for(int k = 0; k < photoNum; k++) {
                        if(bitmaps[k] == null) {
                            allDownloaded = false;
                        }
                    }
                    if (allDownloaded) {
                        Log.d("photos", "all photos are downloaded");
                    }


                    photoAdapter = new PhotoAdapter(new ArrayList<>(Arrays.asList(bitmaps)));
                    photosRecyclerView.setAdapter(photoAdapter);
                    photoMetadataBuffer.release();
                }
            }
        });
    }


}
