package org.winterfell.misc.indigo.renderer.ext.pptx.render.data;

import org.winterfell.misc.indigo.renderer.ext.pptx.support.BufferedImageUtils;
import org.winterfell.misc.indigo.renderer.ext.pptx.support.ByteUtils;
import org.winterfell.misc.indigo.renderer.ext.pptx.support.SsPictureType;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

/**
 * 图片的渲染数据
 *
 * @author alex
 * @version v1.0 2020/11/18
 */
public class SsPictureRenderData implements SsRenderData {

    private static final long serialVersionUID = 5927472717081919014L;

    private int width;
    private int height;
    private byte[] image;
    private SsPictureType pictureType;
    /**
     * When the picture does not exist, the altMeta displayed
     */
    private String altMeta = "";

    /**
     * of local
     *
     * @param path path of image file
     * @return
     */
    public static SsPictureRenderData ofLocal(String path) {
        return ofBytes(ByteUtils.getLocalByteArray(new File(path)), SsPictureType.suggestFileType(path));
    }

    /**
     * of url
     *
     * @param url         image url
     * @param pictureType image type
     * @return
     */
    public static SsPictureRenderData ofUrl(String url, SsPictureType pictureType) {
        return ofBytes(ByteUtils.getUrlByteArray(url), pictureType);
    }

    /**
     * of bytes
     *
     * @param bytes       byte[]
     * @param pictureType image type
     * @return
     */
    public static SsPictureRenderData ofBytes(byte[] bytes, SsPictureType pictureType) {
        return new SsPictureRenderData().setImage(bytes).setPictureType(pictureType);
    }

    public SsPictureRenderData() {
    }

    /**
     * default
     *
     * @param width
     * @param height
     * @param image
     * @param pictureType
     * @param altMeta
     */
    public SsPictureRenderData(int width, int height, byte[] image, SsPictureType pictureType, String altMeta) {
        this.width = width;
        this.height = height;
        this.image = image;
        this.pictureType = pictureType;
        this.altMeta = altMeta;
    }

    /**
     * create data by local ile path
     *
     * @param width
     * @param height
     * @param path
     */
    public SsPictureRenderData(int width, int height, String path) {
        this(width, height, new File(path));
    }

    /**
     * create data by local file
     *
     * @param width
     * @param height
     * @param picture
     */
    public SsPictureRenderData(int width, int height, File picture) {
        this(width, height, ByteUtils.getLocalByteArray(picture), SsPictureType.suggestFileType(picture.getPath()));
    }

    /**
     * create data by byte[]
     *
     * @param width
     * @param height
     * @param image
     * @param pictureType
     */
    public SsPictureRenderData(int width, int height, byte[] image, SsPictureType pictureType) {
        this.width = width;
        this.height = height;
        this.image = image;
        this.pictureType = pictureType;
    }

    /**
     * create data by stream
     *
     * @param width
     * @param height
     * @param pictureType
     * @param stream
     */
    public SsPictureRenderData(int width, int height, SsPictureType pictureType, InputStream stream) {
        this(width, height, ByteUtils.toByteArray(stream), pictureType);
    }

    /**
     * create data by buffered image
     *
     * @param width
     * @param height
     * @param pictureType
     * @param image
     */
    public SsPictureRenderData(int width, int height, SsPictureType pictureType, BufferedImage image) {
        this(width, height, BufferedImageUtils.getBufferByteArray(image, pictureType.format()), pictureType);
    }

    public int getWidth() {
        return width;
    }

    public SsPictureRenderData setWidth(int width) {
        this.width = width;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public SsPictureRenderData setHeight(int height) {
        this.height = height;
        return this;
    }

    public byte[] getImage() {
        return image;
    }

    public SsPictureRenderData setImage(byte[] image) {
        this.image = image;
        return this;
    }

    public SsPictureType getPictureType() {
        return pictureType;
    }

    public SsPictureRenderData setPictureType(SsPictureType pictureType) {
        this.pictureType = pictureType;
        return this;
    }

    public String getAltMeta() {
        return altMeta;
    }

    public SsPictureRenderData setAltMeta(String altMeta) {
        this.altMeta = altMeta;
        return this;
    }
}