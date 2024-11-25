package com.lastterm.finalexam.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.lastterm.finalexam.R;
import com.lastterm.finalexam.data.entities.Room;
import com.lastterm.finalexam.data.repositories.RoomRepository;

import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private final List<Room> roomList;
    private final Context context;

    public RoomAdapter(List<Room> roomList, Context context) {
        this.roomList = roomList;
        this.context = context;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = roomList.get(position);
        holder.roomTitle.setText(room.getTitle());
        holder.roomPrice.setText(String.format("%,.2f VND", room.getPrice()));
        holder.roomAddress.setText(room.getAddress());

        holder.menuIcon.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.menuIcon);
            popupMenu.inflate(R.menu.popup_room_management_menu);

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int id = menuItem.getItemId();
                if (id == R.id.action_select) {
                    Toast.makeText(context, "Select " + room.getTitle(), Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.action_edit) {
                    Toast.makeText(context, "Edit " + room.getTitle(), Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.action_delete) {
                    Toast.makeText(context, "Delete " + room.getTitle(), Toast.LENGTH_SHORT).show();
                    roomList.remove(holder.getAdapterPosition());
                    notifyItemRemoved(holder.getAdapterPosition());
                    return true;
                }
                return false;
            });

            popupMenu.show();
        });

        holder.heartIcon.setOnClickListener(view -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            RoomRepository roomRepository = new RoomRepository();
            Log.d("Room: ", room.getId());
            boolean isFavorite = false;

            roomRepository.addToFavorites(room.getId(),auth.getCurrentUser().getUid(), (s) -> {
                if (s) {
                    Toast.makeText(context, "Đã thêm vào mục yêu thích", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Đã có trong mục yêu thích", Toast.LENGTH_SHORT).show();
                }
            }, (e) -> {Log.d("fail: ", e.getMessage());});

        });
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {

        TextView roomTitle, roomPrice, roomAddress;
        ImageView menuIcon, heartIcon;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            roomTitle = itemView.findViewById(R.id.roomTitle);
            roomPrice = itemView.findViewById(R.id.roomPrice);
            roomAddress = itemView.findViewById(R.id.roomAddress);
            menuIcon = itemView.findViewById(R.id.menuIcon);
            heartIcon = itemView.findViewById(R.id.heartIcon);
        }
    }

}
