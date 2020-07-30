package world.facesof.issue160621990;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

public class ImageStackAttributed extends AppCompatImageView {
	private static final int NUMBER_OF_IMAGES = 4;
	private static final float SHADOW_RADIUS = 8.0f;
	private static final float SHADOW_X = 0.0f;
	private static final float SHADOW_Y = 6.0f;

	private static final int OFFSET_X = 7;
	private static final int OFFSET_Y = 4;

	private final Bitmap[] mImages = new Bitmap[NUMBER_OF_IMAGES];
	private final Target[] mLoaderTargets = new Target[NUMBER_OF_IMAGES];

	private final int mImageSide;
	private final int mOffsetX;
	private final int mOffsetY;
	private final Paint mPaint;
	private final Paint mShadowPaint;
	private final Rect mRect;
	private final Rect mImageRect;
	private final Bitmap mNoCoverBitmap;
	private final Bitmap mEmptyStackBorderBitmap;

	public ImageStackAttributed(Context context) {
		this(context, null);
	}

	public ImageStackAttributed(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ImageStackAttributed(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		setClickable(false);

		mImageSide = context.getResources().getDimensionPixelSize(R.dimen.image_side);
		mOffsetX = (int)Utils.convertDpToPixel(OFFSET_X, context);
		mOffsetY = (int)Utils.convertDpToPixel(OFFSET_Y, context);

		mNoCoverBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.no_cover);

		mEmptyStackBorderBitmap = drawableToBitmap(getResources().getDrawable(R.drawable.empty_stack_border_attributed, null));

		setLayerType(LAYER_TYPE_SOFTWARE, null);

		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			mShadowPaint.setColor(getResources().getColor(R.color.image_stack_shadow, null));
		}
		else {
			mShadowPaint.setColor(getResources().getColor(R.color.image_stack_shadow));
		}

		mShadowPaint.setShadowLayer(SHADOW_RADIUS, SHADOW_X, SHADOW_Y, Color.BLACK);
		mRect = new Rect();
		mImageRect = new Rect();

		for (int i = 0; i < NUMBER_OF_IMAGES; i++) {
			mLoaderTargets[i] = new ImageLoaderTarget(i);
		}
	}

	public void setImages(String... urls) {
		Picasso picasso = Picasso.get();

		for (int i = 0; i < NUMBER_OF_IMAGES; i++) {
			picasso.cancelRequest(mLoaderTargets[i]);
			mImages[i] = mNoCoverBitmap;
		}

		invalidate();

		if (urls.length > 0) {
			for (int i = 0; i < Math.min(mImages.length, urls.length); ++i) {
				picasso.load(urls[i]).placeholder(R.drawable.no_cover).into(mLoaderTargets[i]);
			}
		}
		else {
			Arrays.fill(mImages, mEmptyStackBorderBitmap);
			invalidate();
		}
	}

	@Override
	protected void onDraw(@NonNull Canvas canvas) {
		super.onDraw(canvas);

		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

		int x;
		int y;

		for (int i = mImages.length - 1; i >= 0; i--) {
			Bitmap image = mImages[i];

			x = mOffsetX * i;
			y = mOffsetY * i;

			mRect.set(x, y, mImageSide + x, mImageSide + y);

			canvas.drawRect(mRect, mShadowPaint);

			if (image != null) {
				if (!image.isRecycled()) {
					mImageRect.set(0, 0, image.getWidth(), image.getHeight());
					canvas.drawBitmap(image, mImageRect, mRect, mPaint);
				}
			}
		}
	}

	private static Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			BitmapDrawable bitmapDrawable = (BitmapDrawable)drawable;

			if (bitmapDrawable.getBitmap() != null) {
				return bitmapDrawable.getBitmap();
			}
		}

		Bitmap bitmap;

		if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
			bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
		}
		else {
			bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
		}

		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	private class ImageLoaderTarget implements Target { // com.squareup.picasso.Target
		private final int mPosition;

		ImageLoaderTarget(int position) {
			mPosition = position;
		}

		@Override
		public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
			if (mImages[mPosition] != null && mImages[mPosition] != mNoCoverBitmap && mImages[mPosition] != bitmap) {
				mImages[mPosition].recycle();
			}

			mImages[mPosition] = bitmap;
			invalidate();
		}

		@Override
		public void onBitmapFailed(Exception e, Drawable errorDrawable) {
			invalidate();
		}

		@Override
		public void onPrepareLoad(Drawable placeHolderDrawable) {
		}
	}
}
