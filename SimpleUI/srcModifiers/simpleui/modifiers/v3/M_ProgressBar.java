package simpleui.modifiers.v3;

import simpleui.modifiers.ModifierInterface;
import simpleui.modifiers.v1.uiDecoration.UiDecoratable;
import simpleui.modifiers.v1.uiDecoration.UiDecorator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.googlecode.simpleui.library.R;

public abstract class M_ProgressBar implements ModifierInterface, UiDecoratable {

	private ProgressBar progressBar;
	private UiDecorator myDecorator;
	private TextView nameText;
	private LinearLayout container;
	private final boolean clickable;

	public M_ProgressBar() {
		this(false);
	}

	/**
	 * Use {@link M_Slider} instead
	 * 
	 * @param clickable
	 */
	@Deprecated
	public M_ProgressBar(boolean clickable) {
		this.clickable = clickable;
	}

	public void hide() {
		if (container != null) {
			container.setVisibility(View.GONE);
		}
	}

	public void showAgain() {
		if (container != null) {
			container.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public View getView(Context context) {
		container = new LinearLayout(context);
		container.setOrientation(LinearLayout.VERTICAL);
		container.setGravity(Gravity.CENTER_VERTICAL);

		nameText = new TextView(context);
		nameText.setText(getVarName());
		nameText.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 2));
		container.addView(nameText);

		progressBar = (ProgressBar) View.inflate(context,
				R.layout.material_factory_progressbar_horizontal, null);
		progressBar.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1));
		progressBar.setProgress(loadInitValue());
		final int maxValue = loadMaxValue();
		progressBar.setMax(maxValue);

		if (clickable) {
			progressBar.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					updateValue((int) (event.getX() / v.getWidth() * maxValue),
							null);
					return false;
				}
			});
		}

		container.addView(progressBar);
		container.setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING,
				DEFAULT_PADDING);

		if (myDecorator != null) {
			int currentLevel = myDecorator.getCurrentLevel();
			myDecorator.decorate(context, nameText, currentLevel + 1,
					UiDecorator.TYPE_INFO_TEXT);
			myDecorator.decorate(context, progressBar, currentLevel + 1,
					UiDecorator.TYPE_EDIT_TEXT);
		}
		return container;
	}

	@Override
	public boolean assignNewDecorator(UiDecorator decorator) {
		myDecorator = decorator;
		return true;
	}

	@Override
	public boolean save() {
		return true;
	}

	public abstract int loadInitValue();

	public abstract int loadMaxValue();

	public abstract String getVarName();

	private final Handler mHandler = new Handler(Looper.getMainLooper());

	public void updateValue(final int newProgressValue, final String updatedText) {
		// do it from the UI thread:
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				progressBar.setProgress(newProgressValue);
				if (updatedText != null) {
					nameText.setText(updatedText);
				}
			}
		});
	}

	public int getProgressValue() {
		return progressBar.getProgress();
	}

}
