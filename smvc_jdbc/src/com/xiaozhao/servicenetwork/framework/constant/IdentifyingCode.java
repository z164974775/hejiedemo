package com.xiaozhao.servicenetwork.framework.constant;


import java.awt.Color;  
import java.awt.Graphics2D;  
import java.awt.geom.AffineTransform;  
import java.util.Random;  
  
/** 
 * 验证码图片生成器 
 *  
 * @author HeJie 
 *  
 */  
public class IdentifyingCode {  
    /** 
     * 验证码图片的宽度。 
     */  
    private int width = 100;  
    /** 
     * 验证码图片的高度。 
     */  
    private int height = 30;  
    /** 
     * 验证码的数量。 
     */  
    private Random random = new Random();  
      
    public IdentifyingCode(){}  
    /** 
     * 生成随机颜色 
     * @param fc    前景色 
     * @param bc    背景色 
     * @return  Color对象，此Color对象是RGB形式的。 
     */  
    public Color getRandomColor(int fc, int bc) {  
        if (fc > 255)  
            fc = 200;  
        if (bc > 255)  
            bc = 255;  
        int r = fc + random.nextInt(bc - fc);  
        int g = fc + random.nextInt(bc - fc);  
        int b = fc + random.nextInt(bc - fc);  
        return new Color(r, g, b);  
    }  
      
    /** 
     * 绘制干扰线 
     * @param g Graphics2D对象，用来绘制图像 
     * @param nums  干扰线的条数 
     */  
    public void drawRandomLines(Graphics2D g ,int nums ){  
        g.setColor(this.getRandomColor(160, 200)) ;  
        for(int i=0 ; i<nums ; i++){  
            int x1 = random.nextInt(width) ;  
            int y1 = random.nextInt(height);  
            int x2 = random.nextInt(12) ;  
            int y2 = random.nextInt(12) ;  
            g.drawLine(x1, y1, x2, y2) ;  
        }  
    }  
      
    /** 
     * 获取随机字符串， 
     *      此函数可以产生由大小写字母，汉字，数字组成的字符串 
     * @param length    随机字符串的长度 
     * @return  随机字符串 
     */  
    public String drawRandomString(int length , Graphics2D g){  
        StringBuffer strbuf = new StringBuffer() ;  
        String temp = "" ;
        String[] str = {"传家之宝","千军万马","龙飞凤舞","龙马精神","见多识广","积少成多","千变万化","五花八门","鹏程万里","欢呼雀跃","命中注定","九牛一毛","风雨同舟","百里挑一","川流不息","美不胜收","滔滔不绝","大言不惭","落落大方","豪言壮语"};//初始数据
        String strcy = str[random.nextInt(str.length)];
        for(int i=0 ; i<4 ; i++){  
             temp= strcy.substring(i,i+1);                           
            //设置字体颜色    
            Color color = new Color(random.nextInt(200), random.nextInt(200), random.nextInt(200));  
            g.setColor(color);
                
            //想文字旋转一定的角度  
            AffineTransform trans = new AffineTransform();
            int num = random.nextInt(80)-40;
            trans.rotate(num*3.14/450,15*i+8,10);
            
            //缩放文字  
            //float scaleSize = random.nextFloat() + 0.8f ;  
            //if(scaleSize>1f)  
                //scaleSize = 1f ;  
           // trans.scale(scaleSize, scaleSize) ; 
            
            g.setTransform(trans) ;
            g.drawString(temp,20*i+10, 20) ;  
              
            strbuf.append(temp) ;  
        }  
        g.dispose() ;  
        return strbuf.toString() ;  
    }  
    public int getWidth() {  
        return width;  
    }  
    public void setWidth(int width) {  
        this.width = width;  
    }  
    public int getHeight() {  
        return height;  
    }  
    public void setHeight(int height) {  
        this.height = height;  
    }  
} 
