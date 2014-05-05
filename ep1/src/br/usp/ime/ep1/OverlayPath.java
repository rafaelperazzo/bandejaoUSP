package br.usp.ime.ep1;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;
/**
 * Classe que implementa um overlay do tipo caminho.
 * @author Rafael Perazzo, Luiz Carlos e Maciel Caleb
 *
 */
public class OverlayPath extends Overlay{

	private GeoPoint p1;
	private GeoPoint p2;
	private int cor = Color.BLACK;
	private int espessura = 1;
	
	public OverlayPath(GeoPoint p1, GeoPoint p2) {
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public OverlayPath(GeoPoint p1, GeoPoint p2, int cor) {
		this.p1 = p1;
		this.p2 = p2;
		this.cor = cor;
	}
	
	public OverlayPath(GeoPoint p1, GeoPoint p2, int cor, int espessura) {
		this.p1 = p1;
		this.p2 = p2;
		this.cor = cor;
		this.espessura = espessura;
	}
	
	@Override
	public void draw(Canvas canvas, MapView mv, boolean shadow)
	{
		Path p = new Path();
		p.setFillType(Path.FillType.INVERSE_EVEN_ODD);
		Projection projection = mv.getProjection();
	    Point from = new Point();
	    Point to = new Point();
	    projection.toPixels(p1, from);
	    projection.toPixels(p2, to);
	    p.moveTo(from.x, from.y);
	    p.lineTo(to.x, to.y);
	    Paint mPaint = new Paint();
	    mPaint.setStyle(Style.STROKE);
	    mPaint.setColor(this.cor);
	    mPaint.setStrokeWidth(this.espessura);
	    mPaint.setAntiAlias(true);
	    canvas.drawPath(p, mPaint);
	    //canvas.drawCircle(to.x, to.y, 2, mPaint);
	    super.draw(canvas, mv, shadow);
	}

	public void setCor(int cor) {
		this.cor = cor;
	}
	
}
