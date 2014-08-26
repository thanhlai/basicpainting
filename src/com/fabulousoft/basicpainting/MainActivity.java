package com.fabulousoft.basicpainting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements OnClickListener {
	private DrawingView drawingView;
	private ImageButton currentPaint, drawBtn, eraseBtn, newBtn, saveBtn;
	LinearLayout paintLayout;
	private float smallBrush, mediumBrush, largeBrush;
	File file;
	OutputStream os;
	Bitmap bitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		drawingView = (DrawingView) findViewById(R.id.drawing);
		paintLayout = (LinearLayout) findViewById(R.id.paint_colors);
		currentPaint = (ImageButton) paintLayout.getChildAt(0);
		currentPaint.setImageDrawable(getResources().getDrawable(
				R.drawable.paint_pressed));
		smallBrush = getResources().getInteger(R.integer.small_size);
		mediumBrush = getResources().getInteger(R.integer.medium_size);
		largeBrush = getResources().getInteger(R.integer.large_size);
		drawBtn = (ImageButton) findViewById(R.id.draw_btn);
		drawBtn.setOnClickListener(this);
		drawingView.setBrushSize(mediumBrush);
		eraseBtn = (ImageButton) findViewById(R.id.erase_btn);
		eraseBtn.setOnClickListener(this);
		newBtn = (ImageButton) findViewById(R.id.new_btn);
		newBtn.setOnClickListener(this);
		saveBtn = (ImageButton) findViewById(R.id.save_btn);
		saveBtn.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void paintClicked(View view) {
		drawingView.setErase(false);
		drawingView.setBrushSize(drawingView.getLastBrushSize());
		if (view != currentPaint) {
			ImageButton imgView = (ImageButton) view;
			String color = view.getTag().toString();
			drawingView.setColor(color);
			imgView.setImageDrawable(getResources().getDrawable(
					R.drawable.paint_pressed));
			currentPaint.setImageDrawable(getResources().getDrawable(
					R.drawable.paint));
			currentPaint = (ImageButton) view;
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.draw_btn) {

			final Dialog brushDialog = new Dialog(this);
			brushDialog.setTitle("Brush size: ");
			brushDialog.setContentView(R.layout.brush_chooser);
			ImageButton smallBtn = (ImageButton) brushDialog
					.findViewById(R.id.small_brush);
			smallBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					drawingView.setBrushSize(smallBrush);
					drawingView.setLastBrushSize(smallBrush);
					drawingView.setErase(false);
					brushDialog.dismiss();
				}
			});
			ImageButton mediumBtn = (ImageButton) brushDialog
					.findViewById(R.id.medium_brush);
			mediumBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					drawingView.setBrushSize(mediumBrush);
					drawingView.setLastBrushSize(mediumBrush);
					drawingView.setErase(false);
					brushDialog.dismiss();
				}
			});

			ImageButton largeBtn = (ImageButton) brushDialog
					.findViewById(R.id.large_brush);
			largeBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					drawingView.setBrushSize(largeBrush);
					drawingView.setLastBrushSize(largeBrush);
					drawingView.setErase(false);
					brushDialog.dismiss();
				}
			});
			brushDialog.show();
		} else if (v.getId() == R.id.erase_btn) {
			final Dialog brushDialog = new Dialog(this);
			brushDialog.setTitle("Eraser size:");
			brushDialog.setContentView(R.layout.brush_chooser);
			ImageButton smallBtn = (ImageButton) brushDialog
					.findViewById(R.id.small_brush);
			smallBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					drawingView.setErase(true);
					drawingView.setBrushSize(smallBrush);
					brushDialog.dismiss();
				}
			});
			ImageButton mediumBtn = (ImageButton) brushDialog
					.findViewById(R.id.medium_brush);
			mediumBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					drawingView.setErase(true);
					drawingView.setBrushSize(mediumBrush);
					brushDialog.dismiss();
				}
			});
			ImageButton largeBtn = (ImageButton) brushDialog
					.findViewById(R.id.large_brush);
			largeBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					drawingView.setErase(true);
					drawingView.setBrushSize(largeBrush);
					brushDialog.dismiss();
				}
			});
			brushDialog.show();
		} else if (v.getId() == R.id.new_btn) {
			AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
			newDialog.setTitle("New drawing");
			newDialog
					.setMessage("Start new drawing (you will lose the current drawing)?");
			newDialog.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							drawingView.startNew();
							dialog.dismiss();
						}
					});
			newDialog.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
			newDialog.show();
		} else if (v.getId() == R.id.save_btn) {
			AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
			saveDialog.setTitle("Save drawing");
			saveDialog.setMessage("Save drawing to device Gallery?");
			saveDialog.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

							try {
								file = new File(Environment
										.getExternalStorageDirectory()
										.toString()
										+ "/Pictures", "character.PNG");
								if (file.exists()) {
									file.delete();
								}
								os = new FileOutputStream(file);
								drawingView.invalidate();
								drawingView.getDrawingCache(true);
								drawingView.buildDrawingCache();
								bitmap = drawingView.getDrawingCache();
								bitmap.compress(Bitmap.CompressFormat.PNG, 100,
										os);
								Toast.makeText(
										getApplicationContext(),
										"The character has been saved successfully.",
										Toast.LENGTH_LONG).show();
							} catch (Exception e) {
								Toast.makeText(
										getApplicationContext(),
										"Oops oops! The character could not be saved.",
										Toast.LENGTH_LONG).show();
							}

						}
					});
			saveDialog.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
			saveDialog.show();
		}
	}

}
