package com.project.OWcompTracker;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = Map.class, parentColumns = "id",
        childColumns = "map_id", onDelete = CASCADE))
public class Composition {

   @PrimaryKey(autoGenerate = true)
   @ColumnInfo(name = "id")
   private long mId;

   @ColumnInfo(name = "text")
   private String mText; // composition name

   @ColumnInfo(name = "rating")
   private String mrating; // match rating (0 to 5)

   @ColumnInfo(name = "victory")
   private String mvictory; // match result (won/lost)


   @ColumnInfo(name = "answer")
   private String mAnswer; // tank 0 - name of first tank

   @ColumnInfo(name = "tank1")
   private String mtank1; // tank 1 - name of second tank

   @ColumnInfo(name = "dps0")
   private String mdps0; // dps 0 - name of first dps

   @ColumnInfo(name = "dps1")
   private String mdps1; // dps 1 - name of second dps

   @ColumnInfo(name = "support0")
   private String msupport0; // support 0 - name of first support

   @ColumnInfo(name = "support1")
   private String msupport1; // support 1 - name of second support


   @ColumnInfo(name = "map_id")
   private long mMapId;

   public void setId(long id) {
      mId = id;
   }

   public long getId() {
      return mId;
   }

   public String getText() {
      return mText;
   }

   public void setText(String text) {
      mText = text;
   }

   public String getMrating() {
      return mrating;
   }

   public void setMrating(String mrating) {
      this.mrating = mrating;
   }

   public String getMvictory() {
      return mvictory;
   }

   public void setMvictory(String mvictory) {
      this.mvictory = mvictory;
   }

   public String getAnswer() {
      return mAnswer;
   }

   public void setAnswer(String answer) {
      mAnswer = answer;
   }

   public String getMtank1() {
      return mtank1;
   }

   public void setMtank1(String mtank1) {
      this.mtank1 = mtank1;
   }

   public String getMdps0() {
      return mdps0;
   }

   public void setMdps0(String mdps0) {
      this.mdps0 = mdps0;
   }

   public String getMdps1() {
      return mdps1;
   }

   public void setMdps1(String mdps1) {
      this.mdps1 = mdps1;
   }

   public String getMsupport0() {
      return msupport0;
   }

   public void setMsupport0(String msupport0) {
      this.msupport0 = msupport0;
   }

   public String getMsupport1() {
      return msupport1;
   }

   public void setMsupport1(String msupport1) {
      this.msupport1 = msupport1;
   }

   public long getmMapId() {
      return mMapId;
   }

   public void setmMapId(long mapId) {
      mMapId = mapId;
   }

   public long getMapId() {
      return mMapId;
   }

   public void setMapId(long mapId) {
      mMapId = mapId;
   }
}

