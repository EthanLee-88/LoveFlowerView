package com.example.loveflowerview.view;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

/**
 * 自定义估值器，计算贝塞尔曲线
 *
 */
public class BezierEvaluator implements TypeEvaluator<PointF> {

    private PointF controlPoint1, controlPoint2;

    /**
     * 传入控制点
     *
     * @param cp1 控制点1
     * @param cp2 控制点2
     */
    public BezierEvaluator(PointF cp1, PointF cp2){
        this.controlPoint1 = cp1;
        this.controlPoint2 = cp2;
    }

    /**
     * 贝塞尔三次方公式
     *
     * @param fraction fraction的范围是0~1
     * @param P0 起始点
     * @param P3 终点
     * @return 曲线值
     */
    @Override
    public PointF evaluate(float fraction, PointF P0, PointF P3) {
        PointF pathPoint = new PointF();

        pathPoint.x = P0.x * (1 - fraction) * (1 - fraction)* (1 - fraction) +
                      3 * controlPoint1.x * fraction * (1 - fraction) * (1 - fraction) +
                      3 * controlPoint2.x * fraction * fraction * (1 - fraction) +
                      P3.x * fraction * fraction * fraction;

        pathPoint.y = P0.y * (1 - fraction) * (1 - fraction)* (1 - fraction) +
                3 * controlPoint1.y * fraction * (1 - fraction) * (1 - fraction) +
                3 * controlPoint2.y * fraction * fraction * (1 - fraction) +
                P3.y * fraction * fraction * fraction;

        return pathPoint;
    }
}
