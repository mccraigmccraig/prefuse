package prefuse.render;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;

import prefuse.Constants;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.GraphicsLib;
import prefuse.util.StringLib;
import prefuse.visual.VisualItem;


/**
 * Renderer that draws a label, which consists of a text string,
 * an image, or both.
 * 
 * <p>When created using the default constructor, the renderer attempts
 * to use text from the "label" field. To use a different field, use the
 * appropriate constructor or use the {@link #setTextField(String)} method.
 * To perform custom String selection, subclass this Renderer and override the 
 * {@link #getText(VisualItem)} method. When the text field is
 * <code>null</code>, no text label will be shown.</p>
 * 
 * <p>By default, no image is shown. To show an image, the image field needs
 * to be set, either using the appropriate constructor or the
 * {@link #setImageField(String)} method. The value of the image field should
 * be a text string indicating the location of the image file to use. The
 * string should be either a URL, a file located on the current classpath,
 * or a file on the local filesystem. If found, the image will be managed
 * internally by an {@link ImageFactory} instance, which maintains a
 * cache of loaded images.</p>
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class LabelRenderer extends AbstractShapeRenderer {

    protected ImageFactory m_images = null;
    
    protected String m_labelName = "label";
    protected String m_imageName = null;
    
    protected int m_xAlign = Constants.CENTER;
    protected int m_yAlign = Constants.CENTER;
    
    protected int m_horizBorder = 2;
    protected int m_vertBorder  = 0;
    protected int m_imageMargin = 0;
    protected int m_arcWidth    = 0;
    protected int m_arcHeight   = 0;

    protected int m_maxTextWidth = -1;
    
    /** Transform used to scale and position images */
    AffineTransform m_transform = new AffineTransform();
    
    /** The holder for the currently computed bounding box */
    protected RectangularShape m_bbox  = new Rectangle2D.Double();
    protected Point2D m_pt = new Point2D.Double(); // temp point
    protected Font    m_font; // temp font holder
    

    /**
     * Create a new LabelRenderer. By default the field "label" is used
     * as the field name for looking up text, and no image is used.
     */
    public LabelRenderer() {
    }
    
    /**
     * Create a new LabelRenderer. Draws a text label using the given
     * text data field and does not draw an image.
     * @param textField the data field for the text label.
     */
    public LabelRenderer(String textField) {
        this.setTextField(textField);
    }
    
    /**
     * Create a new LabelRenderer. Draws a text label using the given text
     * data field, and draws the image at the location reported by the
     * given image data field.
     * @param textField the data field for the text label
     * @param imageField the data field for the image location. This value
     * in the data field should be a URL, a file within the current classpath,
     * a file on the filesystem, or null for no image. If the
     * <code>imageField</code> parameter is null, no images at all will be
     * drawn.
     */
    public LabelRenderer(String textField, String imageField) {
        setTextField(textField);
        setImageField(imageField);
    }
    
    // ------------------------------------------------------------------------
    
    /**
     * Rounds the corners of the bounding rectangle in which the text
     * string is rendered. This will only be seen if either the stroke
     * or fill color is non-transparent.
     * @param arcWidth the width of the curved corner
     * @param arcHeight the height of the curved corner
     */
    public void setRoundedCorner(int arcWidth, int arcHeight) {
        if ( (arcWidth == 0 || arcHeight == 0) && 
            !(m_bbox instanceof Rectangle2D) ) {
            m_bbox = new Rectangle2D.Double();
        } else {
            if ( !(m_bbox instanceof RoundRectangle2D) )
                m_bbox = new RoundRectangle2D.Double();
            ((RoundRectangle2D)m_bbox)
                .setRoundRect(0,0,10,10,arcWidth,arcHeight);
            m_arcWidth = arcWidth;
            m_arcHeight = arcHeight;
        }
    }

    /**
     * Get the field name to use for text labels.
     * @return the data field for text labels, or null for no text
     */
    public String getTextField() {
        return m_labelName;
    }
    
    /**
     * Set the field name to use for text labels.
     * @param textField the data field for text labels, or null for no text
     */
    public void setTextField(String textField) {
        m_labelName = textField;
    }
    
    /**
     * Sets the maximum width that should be allowed of the text label.
     * A value of -1 specifies no limit (this is the default).
     * @param maxWidth the maximum width of the text or -1 for no limit
     */
    public void setMaxTextWidth(int maxWidth) {
        m_maxTextWidth = maxWidth;
    }
    
    /**
     * Returns the text to draw. Subclasses can override this class to
     * perform custom text selection.
     * @param item the item to represent as a <code>String</code>
     * @return a <code>String</code> to draw
     */
    protected String getText(VisualItem item) {
        String s = null;
        if ( item.canGetString(m_labelName) ) {
            s = item.getString(m_labelName);
            if ( m_maxTextWidth > -1 ) {
                Font font = item.getFont();
                FontMetrics fm = DEFAULT_GRAPHICS.getFontMetrics(font);
                if ( fm.stringWidth(s) > m_maxTextWidth ) {
                    s = StringLib.abbreviate(s, fm, m_maxTextWidth);       
                }
            }
        }
        return s;
    }

    // ------------------------------------------------------------------------
    // Image Handling
    
    /**
     * Get the data field for image locations. The value stored
     * in the data field should be a URL, a file within the current classpath,
     * a file on the filesystem, or null for no image.
     * @return the data field for image locations, or null for no images
     */
    public String getImageField() {
        return m_imageName;
    }
    
    /**
     * Set the data field for image locations. The value stored
     * in the data field should be a URL, a file within the current classpath,
     * a file on the filesystem, or null for no image. If the
     * <code>imageField</code> parameter is null, no images at all will be
     * drawn.
     * @param imageField the data field for image locations, or null for
     * no images
     */
    public void setImageField(String imageField) {
        if ( imageField != null ) m_images = new ImageFactory();
        m_imageName = imageField;
    }
    
    /**
     * Sets the maximum image dimensions, used to control scaling of loaded
     * images. This scaling is enforced immediately upon loading of the image.
     * @param width the maximum width of images (-1 for no limit)
     * @param height the maximum height of images (-1 for no limit)
     */
    public void setMaxImageDimensions(int width, int height) {
        if ( m_images == null ) m_images = new ImageFactory();
        m_images.setMaxImageDimensions(width, height);
    }
    
    /**
     * Returns a location string for the image to draw. Subclasses can override 
     * this class to perform custom image selection beyond looking up the value
     * from a data field.
     * @param item the item for which to select an image to draw
     * @return the location string for the image to use, or null for no image
     */
    protected String getImageLocation(VisualItem item) {
        return item.canGetString(m_imageName)
                ? item.getString(m_imageName)
                : null;
    }
    
    /**
     * Get the image to include in the label for the given VisualItem.
     * @param item the item to get an image for
     * @return the image for the item, or null for no image
     */
    protected Image getImage(VisualItem item) {
        String imageLoc = getImageLocation(item);
        return ( imageLoc == null ? null : m_images.getImage(imageLoc) );
    }
    
    
    // ------------------------------------------------------------------------
    // Rendering
    
    /**
     * @see prefuse.render.AbstractShapeRenderer#getRawShape(prefuse.visual.VisualItem)
     */
    protected Shape getRawShape(VisualItem item) {
        String text = getText(item);
        Image  img  = getImage(item);
        double size = item.getSize();
        boolean sizeAdjust = (size != 1);
        
        // get image dimensions
        double iw=0, ih=0;
        if ( img != null ) {
            ih = img.getHeight(null);
            iw = img.getWidth(null);    
        }
        
        // get text dimensions
        int tw=0, th=0;
        if ( text != null ) {
            // put item font in temp member variable
            m_font = item.getFont();
            // scale the font as needed
            if ( sizeAdjust ) {
                m_font = FontLib.getFont(m_font.getName(), m_font.getStyle(),
                                         size*m_font.getSize());
            }
            FontMetrics fm = DEFAULT_GRAPHICS.getFontMetrics(m_font);
            th = fm.getHeight();
            tw = fm.stringWidth(text);    
        }
        
        // get bounding box dimensions
        double w = tw + size*(iw + 2*m_horizBorder
                      + (tw>0 && iw>0 ? m_imageMargin : 0));
        double h = Math.max(th, size*ih) + size*2*m_vertBorder;
        
        // get the top-left point, using the current alignment settings
        getAlignedPoint(m_pt, item, w, h, m_xAlign, m_yAlign);
        
        if ( m_bbox instanceof RoundRectangle2D ) {
            RoundRectangle2D rr = (RoundRectangle2D)m_bbox;
            rr.setRoundRect(m_pt.getX(), m_pt.getY(), w, h,
                            size*m_arcWidth, size*m_arcHeight);
        } else {
            m_bbox.setFrame(m_pt.getX(), m_pt.getY(), w, h);
        }
        return m_bbox;
    }
    
    /**
     * Helper method, which calculates the top-left co-ordinate of an item
     * given the item's alignment.
     */
    protected static void getAlignedPoint(Point2D p, VisualItem item, 
            double w, double h, int xAlign, int yAlign)
    {
        double x = item.getX(), y = item.getY();
        if ( Double.isNaN(x) || Double.isInfinite(x) )
            x = 0; // safety check
        if ( Double.isNaN(y) || Double.isInfinite(y) )
            y = 0; // safety check
        
        if ( xAlign == Constants.CENTER ) {
            x = x-(w/2);
        } else if ( xAlign == Constants.RIGHT ) {
            x = x-w;
        }
        if ( yAlign == Constants.CENTER ) {
            y = y-(h/2);
        } else if ( yAlign == Constants.BOTTOM ) {
            y = y-h;
        }
        p.setLocation(x,y);
    }
    
    /**
     * @see prefuse.render.Renderer#render(java.awt.Graphics2D, prefuse.visual.VisualItem)
     */
    public void render(Graphics2D g, VisualItem item) {
        RectangularShape shape = (RectangularShape)getShape(item);
        if ( shape == null ) return;
        
        // fill the shape, if requested
        int type = getRenderType(item);
        if ( type==RENDER_TYPE_FILL || type==RENDER_TYPE_DRAW_AND_FILL )
            GraphicsLib.paint(g, item, shape, RENDER_TYPE_FILL);

        // now render the image and text
        String text = getText(item);
        Image  img  = getImage(item);
        
        if ( text == null && img == null )
            return;
                        
        double size = item.getSize();
        boolean useInt = 1.5 > Math.max(g.getTransform().getScaleX(),
                                        g.getTransform().getScaleY());
        double x = shape.getMinX() + size*m_horizBorder;
            
        // render image
        if ( img != null ) {            
            double w = size * img.getWidth(null);
            double h = size * img.getHeight(null);
            double y = shape.getMinY() + (shape.getHeight()-h)/2;
            
            if ( useInt && size == 1.0 ) {
                // if possible, use integer precision
                // results in faster, flicker-free image rendering
                g.drawImage(img, (int)x, (int)y, null);
            } else {
                m_transform.setTransform(size,0,0,size,x,y);
                g.drawImage(img, m_transform, null);
            }
            
            x += w + (text!=null && w>0 ? size*m_imageMargin : 0);
        }
        
        // render text
        if ( text != null ) {
            int textColor = item.getTextColor();
            if ( ColorLib.alpha(textColor) > 0 ) {
                g.setPaint(ColorLib.getColor(textColor));
                g.setFont(m_font);
                FontMetrics fm = DEFAULT_GRAPHICS.getFontMetrics(m_font);
                double y = shape.getCenterY() - 
                            ((fm.getHeight()>>1)-fm.getAscent());
                if ( useInt ) {
                    // use integer precision unless zoomed-in
                    // results in more stable drawing
                    g.drawString(text, (int)x, (int)y);
                } else {
                    g.drawString(text, (float)x, (float)y);
                }
            }
        }
    
        // draw border
        if (type==RENDER_TYPE_DRAW || type==RENDER_TYPE_DRAW_AND_FILL) {
            Stroke stroke = g.getStroke();
            Stroke itemStroke = getStroke(item);
            if ( itemStroke != null )
                g.setStroke(itemStroke);
            GraphicsLib.paint(g, item, shape, RENDER_TYPE_DRAW);
            g.setStroke(stroke);
        }
    }
    
    /**
     * Returns the image factory used by this renderer.
     * @return the image factory
     */
    public ImageFactory getImageFactory() {
        if ( m_images == null ) m_images = new ImageFactory();
        return m_images;
    }
    
    /**
     * Sets the image factory used by this renderer.
     * @param ifact the image factory
     */
    public void setImageFactory(ImageFactory ifact) {
        m_images = ifact;
    }
    
    /**
     * Get the horizontal alignment of this node with respect to it's
     * location co-ordinate.
     * @return the horizontal alignment, one of
     * {@link prefuse.Constants#LEFT}, {@link prefuse.Constants#RIGHT}, or
     * {@link prefuse.Constants#CENTER}.
     */
    public int getHorizontalAlignment() {
        return m_xAlign;
    }
    
    /**
     * Get the vertical alignment of this node with respect to it's
     * location co-ordinate.
     * @return the vertical alignment, one of
     * {@link prefuse.Constants#TOP}, {@link prefuse.Constants#BOTTOM}, or
     * {@link prefuse.Constants#CENTER}.
     */
    public int getVerticalAlignment() {
        return m_yAlign;
    }
    
    /**
     * Set the horizontal alignment of this node with respect to it's
     * location co-ordinate.
     * @param align the horizontal alignment, one of
     * {@link prefuse.Constants#LEFT}, {@link prefuse.Constants#RIGHT}, or
     * {@link prefuse.Constants#CENTER}.
     */ 
    public void setHorizontalAlignment(int align) {
        m_xAlign = align;
    }
    
    /**
     * Set the vertical alignment of this node with respect to it's
     * location co-ordinate.
     * @param align the vertical alignment, one of
     * {@link prefuse.Constants#TOP}, {@link prefuse.Constants#BOTTOM}, or
     * {@link prefuse.Constants#CENTER}.
     */ 
    public void setVerticalAlignment(int align) {
        m_yAlign = align;
    }
    
    /**
     * Returns the amount of padding in pixels between text 
     * and the border of this item along the horizontal dimension.
     * @return the horizontal padding
     */
    public int getHorizontalPadding() {
        return m_horizBorder;
    }
    
    /**
     * Sets the amount of padding in pixels between text 
     * and the border of this item along the horizontal dimension.
     * @param xpad the horizontal padding to set
     */
    public void setHorizontalPadding(int xpad) {
        m_horizBorder = xpad;
    }
    
    /**
     * Returns the amount of padding in pixels between text 
     * and the border of this item along the vertical dimension.
     * @return the vertical padding
     */
    public int getVerticalPadding() {
        return m_vertBorder;
    }
    
    /**
     * Sets the amount of padding in pixels between text 
     * and the border of this item along the vertical dimension.
     * @param ypad the vertical padding
     */
    public void setVerticalPadding(int ypad) {
        m_vertBorder = ypad;
    }
    
} // end of class LabelRenderer
