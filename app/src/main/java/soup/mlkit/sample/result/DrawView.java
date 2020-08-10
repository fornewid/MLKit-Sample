package soup.mlkit.sample.result;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;
import com.google.firebase.ml.vision.text.FirebaseVisionText;

import java.util.ArrayList;
import java.util.List;

public class DrawView extends View {
    private static final int COLOR_BLUE = Color.argb(127, 0, 0, 255);
    private static final int COLOR_WHITE = Color.WHITE;

    private Rect imageRect = new Rect();
    private float imageScale = 1;
    private final List<FirebaseVisionFace> faces = new ArrayList<>();
    private final List<FirebaseVisionText.Element> texts = new ArrayList<>();
    private final List<FirebaseVisionBarcode> barcodes = new ArrayList<>();
    private float density;
    private Paint paint;

    //컨스트럭터
    public DrawView(Context context) {
        super(context);
        init();
    }

    //컨스트럭터
    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    //컨스트럭터
    public DrawView(Context context, AttributeSet attrs,
                    int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    //초기화
    private void init() {
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        density = metrics.density;
        paint = new Paint();
    }

    public void setImageSize(int imageWidth, int imageHeight) {
        //이미지 표시영역 계산(AspectFill)
        float scale = Math.max((float) getWidth() / (float) imageWidth, (float) getHeight() / (float) imageHeight);
        float dw = imageWidth * scale;
        float dh = imageHeight * scale;
        this.imageRect = new Rect(
                (int) ((getWidth() - dw) / 2),
                (int) ((getHeight() - dh) / 2),
                (int) ((getWidth() - dw) / 2 + dw),
                (int) ((getHeight() - dh) / 2 + dh));
        this.imageScale = scale;
    }

    public void setFaces(@NonNull List<FirebaseVisionFace> faces) {
        this.faces.clear();
        this.faces.addAll(faces);
        invalidate();
    }

    public void setTexts(@NonNull List<FirebaseVisionText.Element> texts) {
        this.texts.clear();
        this.texts.addAll(texts);
        invalidate();
    }

    public void setBarcodes(@NonNull List<FirebaseVisionBarcode> barcodes) {
        this.barcodes.clear();
        this.barcodes.addAll(barcodes);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!faces.isEmpty()) {
            //얼굴검출 그리기
            for (FirebaseVisionFace face : this.faces) {
                //영역 그리기
                Rect rect = convertRect(face.getBoundingBox());
                paint.setColor(COLOR_BLUE);
                paint.setStrokeWidth(2 * density);
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawRect(rect, paint);

                //얼굴 랜드마크 그리기
                paint.setColor(COLOR_WHITE);
                drawLandmark(canvas, face, FirebaseVisionFaceLandmark.LEFT_EYE);
                drawLandmark(canvas, face, FirebaseVisionFaceLandmark.RIGHT_EYE);
                drawLandmark(canvas, face, FirebaseVisionFaceLandmark.MOUTH_LEFT);
                drawLandmark(canvas, face, FirebaseVisionFaceLandmark.MOUTH_RIGHT);
                drawLandmark(canvas, face, FirebaseVisionFaceLandmark.MOUTH_BOTTOM);
            }
        }
        if (!texts.isEmpty()) {
            //텍스트 검출 그리기
            for (FirebaseVisionText.Element element : texts) {
                //영역그리기
                Rect rect = convertRect(element.getBoundingBox());
                paint.setColor(COLOR_BLUE);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawRect(rect, paint);

                //텍스트 그리기
                drawText(canvas, element.getText(), 12, rect);
            }
        }

        //바코드 검출 그리기
        if (!barcodes.isEmpty()) {
            for (FirebaseVisionBarcode barcode : this.barcodes) {
                //영역그리기
                Rect rect = convertRect(barcode.getBoundingBox());
                paint.setColor(COLOR_BLUE);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawRect(rect, paint);

                //바코드 부가정보 그리기
                if (barcode.getRawValue() != null) {
                    drawText(canvas, barcode.getRawValue(), 12, rect);
                }
            }
        }
    }

    //얼굴 랜드마크 그리기
    private void drawLandmark(Canvas canvas, FirebaseVisionFace face, int type) {
        FirebaseVisionFaceLandmark landmark = face.getLandmark(type);
        if (landmark != null) {
            Point point = convertPoint(new Point(
                    landmark.getPosition().getX().intValue(),
                    landmark.getPosition().getY().intValue()));
            paint.setColor(COLOR_WHITE);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(point.x, point.y, 3 * density, paint);
        }
    }

    //텍스트 그리기
    private void drawText(Canvas canvas, String text, float fontSize, Rect rect) {
        if (text == null) return;
        paint.setColor(COLOR_WHITE);
        paint.setTextSize(fontSize * density);
        Paint.FontMetrics metrics = paint.getFontMetrics();
        canvas.save();
        canvas.clipRect(rect);
        float sw = paint.measureText(text);
        if (rect.width() > sw) {
            canvas.drawText(text, rect.left + (rect.width() - sw) / 2, rect.top - metrics.ascent, paint);
        } else {
            canvas.drawText(text, rect.left, rect.top - metrics.ascent, paint);
        }
        canvas.restore();
    }

    //검출결과 좌표계를 화면 좌표계로 변환
    private Rect convertRect(Rect rect) {
        return new Rect(
                (int) (imageRect.left + rect.left * imageScale),
                (int) (imageRect.top + rect.top * imageScale),
                (int) (imageRect.left + rect.left * imageScale + rect.width() * imageScale),
                (int) (imageRect.top + rect.top * imageScale + rect.height() * imageScale));
    }

    //검출결과 좌표계를 화면 좌표계로 변환
    private Point convertPoint(Point point) {
        return new Point(
                (int) (imageRect.left + point.x * imageScale),
                (int) (imageRect.top + point.y * imageScale));
    }
}
