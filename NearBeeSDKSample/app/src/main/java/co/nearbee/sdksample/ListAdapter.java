package co.nearbee.sdksample;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import co.nearbee.models.BeaconAttachment;
import co.nearbee.models.NearBeacon;

/**
 * Copyright (C) 2018 Mobstac, Inc.
 * All rights reserved
 *
 * @author Kislay
 * @since 02/11/18
 */
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.BeaconViewHolder> {

    private ArrayList<NearBeacon> beacons;

    ListAdapter(ArrayList<NearBeacon> beacons) {
        this.beacons = beacons;
    }

    @NonNull
    @Override
    public BeaconViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.list_item_beacon,
                viewGroup,
                false
        );
        return new BeaconViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final BeaconViewHolder viewHolder, int i) {
        final NearBeacon beacon = beacons.get(i);
        final BeaconAttachment attachment = beacon.getBestAvailableAttachment(viewHolder.itemView.getContext());
        viewHolder.title.setText(attachment.getTitle());
        viewHolder.description.setText(attachment.getDescription());
        viewHolder.link.setText(attachment.getUrl());
        Glide.with(viewHolder.icon.getContext())
                .load(attachment.getIconURL())
                .into(viewHolder.icon);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                beacon.launchUrl(view.getContext(), attachment);
            }
        });
    }

    class BeaconViewHolder extends RecyclerView.ViewHolder {

        private TextView title, description, link;
        private AppCompatImageView icon;

        public BeaconViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            link = itemView.findViewById(R.id.link);
            icon = itemView.findViewById(R.id.icon);
        }
    }

    @Override
    public int getItemCount() {
        return beacons.size();
    }
}
