package prefuse.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;

import prefuse.Alignment;
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
 * <code>null</code>, no text label will be shown. Labels can span multiple
 * lines of text, determined by the presence of newline characters ('\n')
 * within the text string.</p>
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
 * <p>The position of the image relative to text can be set using the
 * {@link #setImagePosition(Alignment)} method. Images can be placed to the
 * left, right, above, or below the text. The horizontal and vertical
 * alignments of either the text or the image can be set explicitly
 * using the appropriate methods of this class (e.g.,
 * {@link #setHorizontalTextAlignment(Alignment)}). By default, both the
 * text and images are centered along both the horizontal and
 * vertical directions.</p>
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class LabelRenderer extends AbstractShapeRenderer {

    protected ImageFactory m_images = null;
    protected String m_delim = "\n";

    protected String m_labelName = "label";
    protected String m_imageName = null;

    protected Alignment m_xAlign = Alignment.CENTER;
    protected Alignment m_yAlign = Alignment.CENTER;
    protected Alignment m_hTextAlign = Alignment.CENTER;
    protected Alignment m_vTextAlign = Alignment.CENTER;
    protected Alignment m_hImageAlign = Alignment.CENTER;
    protected Alignment m_vImageAlign = Alignment.CENTER;
    protected Alignment m_imagePos = Alignment.LEFT;

    protected int m_horizBorder = 2;
    protected int m_vertBorder  = 0;
    protected int m_imageMargin = 2;
    protected int m_arcWidth    = 0;
    protected int m_arcHeight   = 0;

    protected int m_maxTextWidth = -1;

	protected Integer maxLineLength = null;

    /** Transform used to scale and position images */
    AffineTransform m_transform = new AffineTransform();

    /** The holder for the currently computed bounding box */
    protected RectangularShape m_bbox  = new Rectangle2D.Double();
    protected Point2D m_pt = new Point2D.Double(); // temp point
    protected Font    m_font; // temp font holder
    protected String    m_text; // label text
    protected Dimension m_textDim = new Dimension(); // text width / height

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
            if ( !(m_bbox instanceof RoundRectangle2D) ) {
				m_bbox = new RoundRectangle2D.Double();
			}
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
    protected String getText(VisualItem<?> item) {
		String orig = null;
		if (item.canGetString(m_labelName)) {
			orig = item.getString(m_labelName);
		}
		if (orig == null || maxLineLength == null || orig.length() < maxLineLength) {
			return orig;
		}

		StringBuffer buf = new StringBuffer();

		String[] origLines = orig.split(m_delim);

		// find the longest word
		int maxLen = 0;
		for (String element : origLines) {
			String[] words = element.split("\\s+");
			for (String element2 : words) {
				if (element2.length() > maxLen) {
					maxLen = element2.length();
				}
			}
		}

		if (maxLineLength > maxLen) {
			maxLen = maxLineLength;
		}

		for (String element : origLines) {
			if (buf.length() > 0) {
				buf.append('\n');
			}
			String[] words = element.split("\\s+");
			int lineLen = 0;
			for (String word : words) {
				if (lineLen > 0) {
					if (lineLen + 1 + word.length() > maxLen) {
						buf.append('\n');
						lineLen = 0;
					} else {
						buf.append(' ');
					}
				}
				buf.append(word);
				lineLen += word.length();
			}
		}
		return buf.toString();

	}

    // ------------------------------------------------------------------------
    // Image Handling

    /**
	 * Get the data field for image locations. The value stored in the data
	 * field should be a URL, a file within the current classpath, a file on the
	 * filesystem, or null for no image.
	 *
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
        if ( imageField != null ) {
			m_images = new ImageFactory();
		}
        m_imageName = imageField;
    }

    /**
     * Sets the maximum image dimensions, used to control scaling of loaded
     * images. This scaling is enforced immediately upon loading of the image.
     * @param width the maximum width of images (-1 for no limit)
     * @param height the maximum height of images (-1 for no limit)
     */
    public void setMaxImageDimensions(int width, int height) {
        if ( m_images == null ) {
			m_images = new ImageFactory();
		}
        m_images.setMaxImageDimensions(width, height);
    }

    /**
     * Returns a location string for the image to draw. Subclasses can override
     * this class to perform custom image selection beyond looking up the value
     * from a data field.
     * @param item the item for which to select an image to draw
     * @return the location string for the image to use, or null for no image
     */
    protected String getImageLocation(VisualItem<?> item) {
        return item.canGetString(m_imageName)
                ? item.getString(m_imageName)
                : null;
    }

    /**
     * Get the image to include in the label for the given VisualItem.
     * @param item the item to get an image for
     * @return the image for the item, or null for no image
     */
    protected Image getImage(VisualItem<?> item) {
        String imageLoc = getImageLocation(item);
        return imageLoc == null ? null : m_images.getImage(imageLoc);
    }


    // ------------------------------------------------------------------------
    // Rendering

    private String computeTextDimensions(VisualItem<?> item, String text,
                                         double size)
    {
        // put item font in temp member variable
        m_font = item.getFont();
        // scale the font as needed
        if ( size != 1 ) {
            m_font = FontLib.getFont(m_font.getName(), m_font.getStyle(),
                                     size*m_font.getSize());
        }

        FontMetrics fm = DEFAULT_GRAPHICS.getFontMetrics(m_font);
        StringBuffer str = null;

        // compute the number of lines and the maximum width
        int nlines = 1, w = 0, start = 0, end = text.indexOf(m_delim);
        m_textDim.width = 0;
        String line;
        for ( ; end >= 0; ++nlines ) {
            w = fm.stringWidth(line=text.substring(start,end));
            // abbreviate line as needed
            if ( m_maxTextWidth > -1 && w > m_maxTextWidth ) {
                if ( str == null ) {
					str = new StringBuffer(text.substring(0,start));
				}
                str.append(StringLib.abbreviate(line, fm, m_maxTextWidth));
                str.append(m_delim);
                w = m_maxTextWidth;
            } else if ( str != null ) {
                str.append(line).append(m_delim);
            }
            // update maximum width and substring indices
            m_textDim.width = Math.max(m_textDim.width, w);
            start = end+1;
            end = text.indexOf(m_delim, start);
        }
        w = fm.stringWidth(line=text.substring(start));
        // abbreviate line as needed
        if ( m_maxTextWidth > -1 && w > m_maxTextWidth ) {
            if ( str == null ) {
				str = new StringBuffer(text.substring(0,start));
			}
            str.append(StringLib.abbreviate(line, fm, m_maxTextWidth));
            w = m_maxTextWidth;
        } else if ( str != null ) {
            str.append(line);
        }
        // update maximum width
        m_textDim.width = Math.max(m_textDim.width, w);

        // compute the text height
        m_textDim.height = fm.getHeight() * nlines;

        return str==null ? text : str.toString();
    }

    /**
     * @see prefuse.render.AbstractShapeRenderer#getRawShape(prefuse.visual.VisualItem)
     */
    @Override
	protected Shape getRawShape(VisualItem<?> item) {
        m_text = getText(item);
        double size = item.getSize();

        // get text dimensions
        int tw=0, th=0;
        if ( m_text != null ) {
            m_text = computeTextDimensions(item, m_text, size);
            th = m_textDim.height;
            tw = m_textDim.width;
        }

        // get image dimensions
        final Image  img  = getImage(item);
        final double iw, ih;
        if ( img != null ) {
            ih = img.getHeight(null);
            iw = img.getWidth(null);
        } else {
        	iw = 0;
        	ih = 0;
        }

        // get bounding box dimensions
        double w=0, h=0;
        switch ( m_imagePos ) {
        case LEFT:
        case RIGHT:
            w = tw + size*(iw +2*m_horizBorder
                   + (tw>0 && iw>0 ? m_imageMargin : 0));
            h = Math.max(th, size*ih) + size*2*m_vertBorder;
            break;
        case TOP:
        case BOTTOM:
            w = Math.max(tw, size*iw) + size*2*m_horizBorder;
            h = th + size*(ih + 2*m_vertBorder
                   + (th>0 && ih>0 ? m_imageMargin : 0));
            break;
        default:
            throw new IllegalStateException(
                "Unrecognized image alignment setting.");
        }

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
    protected static void getAlignedPoint(Point2D p, VisualItem<?> item,
            double w, double h, Alignment xAlign, Alignment yAlign)
    {
        double x = item.getX(), y = item.getY();
        if ( Double.isNaN(x) || Double.isInfinite(x) ) {
			x = 0; // safety check
		}
        if ( Double.isNaN(y) || Double.isInfinite(y) ) {
			y = 0; // safety check
		}

        if ( xAlign == Alignment.CENTER ) {
            x = x-w/2;
        } else if ( xAlign == Alignment.RIGHT ) {
            x = x-w;
        }
        if ( yAlign == Alignment.CENTER ) {
            y = y-h/2;
        } else if ( yAlign == Alignment.BOTTOM ) {
            y = y-h;
        }
        p.setLocation(x,y);
    }

    protected Color getTextColor(VisualItem<?> item) {
    	return ColorLib.getColor(item.getTextColor());
    }

    /**
     * @see prefuse.render.Renderer#render(java.awt.Graphics2D, prefuse.visual.VisualItem)
     */
    @Override
	public void render(Graphics2D g, VisualItem<?> item) {
        RectangularShape shape = (RectangularShape)getShape(item);
        if ( shape == null ) {
			return;
		}

        final Color strokeColor = getStrokeColor(item);
        final Color fillColor = getFillColor(item);
        final BasicStroke stroke = getStroke(item);

        // fill the shape, if requested
        RenderType type = getRenderType(item);
        if ( type==RenderType.FILL || type==RenderType.DRAW_AND_FILL ) {
			GraphicsLib.paint(g, strokeColor, fillColor, shape, stroke, RenderType.FILL);
		}

        // now render the image and text
        String text = m_text;
        Image  img  = getImage(item);

        if ( text == null && img == null ) {
			return;
		}

        double size = item.getSize();
        boolean useInt = 1.5 > Math.max(g.getTransform().getScaleX(),
                                        g.getTransform().getScaleY());
        double x = shape.getMinX() + size*m_horizBorder;
        double y = shape.getMinY() + size*m_vertBorder;

        // render image
        if ( img != null ) {
            double w = size * img.getWidth(null);
            double h = size * img.getHeight(null);
            double ix=x, iy=y;

            // determine one co-ordinate based on the image position
            switch ( m_imagePos ) {
            case LEFT:
                x += w + size*m_imageMargin;
                break;
            case RIGHT:
                ix = shape.getMaxX() - size*m_horizBorder - w;
                break;
            case TOP:
                y += h + size*m_imageMargin;
                break;
            case BOTTOM:
                iy = shape.getMaxY() - size*m_vertBorder - h;
                break;
            default:
                throw new IllegalStateException(
                        "Unrecognized image alignment setting.");
            }

            // determine the other coordinate based on image alignment
            switch ( m_imagePos ) {
            case LEFT:
            case RIGHT:
                // need to set image y-coordinate
                switch ( m_vImageAlign ) {
                case TOP:
                    break;
                case BOTTOM:
                    iy = shape.getMaxY() - size*m_vertBorder - h;
                    break;
                case CENTER:
                    iy = shape.getCenterY() - h/2;
                    break;
                }
                break;
            case TOP:
            case BOTTOM:
                // need to set image x-coordinate
                switch ( m_hImageAlign ) {
                case LEFT:
                    break;
                case RIGHT:
                    ix = shape.getMaxX() - size*m_horizBorder - w;
                    break;
                case CENTER:
                    ix = shape.getCenterX() - w/2;
                    break;
                }
                break;
            }

            if ( useInt && size == 1.0 ) {
                // if possible, use integer precision
                // results in faster, flicker-free image rendering
                g.drawImage(img, (int)ix, (int)iy, null);
            } else {
                m_transform.setTransform(size,0,0,size,ix,iy);
                g.drawImage(img, m_transform, null);
            }
        }

        // render text
        final Color textColor = getTextColor(item);
        if ( text != null && textColor.getAlpha() > 0 ) {
            g.setPaint(textColor);
            g.setFont(m_font);
            FontMetrics fm = DEFAULT_GRAPHICS.getFontMetrics(m_font);

            // compute available width
            double tw;
            switch ( m_imagePos ) {
            case TOP:
            case BOTTOM:
                tw = shape.getWidth() - 2*size*m_horizBorder;
                break;
            default:
                tw = (img != null ? m_textDim.width : shape.getWidth() - 2*size*m_horizBorder);
            }

            // compute available height
            double th;
            switch ( m_imagePos ) {
            case LEFT:
            case RIGHT:
                th = shape.getHeight() - 2*size*m_vertBorder;
                break;
            default:
                th = (img != null ? m_textDim.height : shape.getHeight() - 2*size*m_vertBorder);
            }

            // compute starting y-coordinate
            y += fm.getAscent();
            switch ( m_vTextAlign ) {
            case TOP:
                break;
            case BOTTOM:
                y += th - m_textDim.height;
                break;
            case CENTER:
                y += (th - m_textDim.height)/2;
            }

            // render each line of text
            int lh = fm.getHeight(); // the line height
            int start = 0, end = text.indexOf(m_delim);
            for ( ; end >= 0; y += lh ) {
                drawString(g, fm, text.substring(start, end), useInt, x, y, tw);
                start = end+1;
                end = text.indexOf(m_delim, start);
            }
            drawString(g, fm, text.substring(start), useInt, x, y, tw);
        }

        // draw border
        if (type==RenderType.DRAW || type==RenderType.DRAW_AND_FILL) {
            GraphicsLib.paint(g,strokeColor,fillColor,shape,stroke,RenderType.DRAW);
        }
    }

    private final void drawString(Graphics2D g, FontMetrics fm, String text,
            boolean useInt, double x, double y, double w)
    {
        // compute the x-coordinate
        double tx;
        switch ( m_hTextAlign ) {
        case LEFT:
            tx = x;
            break;
        case RIGHT:
            tx = x + w - fm.stringWidth(text);
            break;
        case CENTER:
            tx = x + (w - fm.stringWidth(text)) / 2;
            break;
        default:
            throw new IllegalStateException(
                    "Unrecognized text alignment setting.");
        }
        // use integer precision unless zoomed-in
        // results in more stable drawing
        if ( useInt ) {
            g.drawString(text, (int)tx, (int)y);
        } else {
            g.drawString(text, (float)tx, (float)y);
        }
    }

    /**
     * Returns the image factory used by this renderer.
     * @return the image factory
     */
    public ImageFactory getImageFactory() {
        if ( m_images == null ) {
			m_images = new ImageFactory();
		}
        return m_images;
    }

    /**
     * Sets the image factory used by this renderer.
     * @param ifact the image factory
     */
    public void setImageFactory(ImageFactory ifact) {
        m_images = ifact;
    }

    // ------------------------------------------------------------------------

    /**
     * Get the horizontal text alignment within the layout. The default is centered text.
     * @return the horizontal text alignment
     */
    public Alignment getHorizontalTextAlignment() {
        return m_hTextAlign;
    }

    /**
     * Set the horizontal text alignment within the layout. One of
     * {@link Alignment#LEFT}, {@link Alignment#RIGHT}, or
     * {@link Alignment#CENTER}. The default is centered text.
     * @param halign the desired horizontal text alignment
     */
    public void setHorizontalTextAlignment(Alignment halign) {
        if ( halign != Alignment.LEFT &&
             halign != Alignment.RIGHT &&
             halign != Alignment.CENTER ) {
			throw new IllegalArgumentException(
			           "Illegal horizontal text alignment value.");
		}
        m_hTextAlign = halign;
    }

    /**
     * Get the vertical text alignment within the layout. One of
     * {@link Alignment#TOP}, {@link Alignment#BOTTOM}, or
     * {@link Alignment#CENTER}. The default is centered text.
     * @return the vertical text alignment
     */
    public Alignment getVerticalTextAlignment() {
        return m_vTextAlign;
    }

    /**
     * Set the vertical text alignment within the layout. One of
     * {@link Alignment#TOP}, {@link Alignment#BOTTOM}, or
     * {@link Alignment#CENTER}. The default is centered text.
     * @param valign the desired vertical text alignment
     */
    public void setVerticalTextAlignment(Alignment valign) {
        if ( valign != Alignment.TOP &&
             valign != Alignment.BOTTOM &&
             valign != Alignment.CENTER ) {
			throw new IllegalArgumentException(
                    "Illegal vertical text alignment value.");
		}
        m_vTextAlign = valign;
    }

    /**
     * Get the horizontal image alignment within the layout. One of
     * {@link Alignment#LEFT}, {@link Alignment#RIGHT}, or
     * {@link Alignment#CENTER}. The default is a centered image.
     * @return the horizontal image alignment
     */
    public Alignment getHorizontalImageAlignment() {
        return m_hImageAlign;
    }

    /**
     * Set the horizontal image alignment within the layout. One of
     * {@link Alignment#LEFT}, {@link Alignment#RIGHT}, or
     * {@link Alignment#CENTER}. The default is a centered image.
     * @param halign the desired horizontal image alignment
     */
    public void setHorizontalImageAlignment(Alignment halign) {
        if ( halign != Alignment.LEFT &&
             halign != Alignment.RIGHT &&
             halign != Alignment.CENTER ) {
			throw new IllegalArgumentException(
			           "Illegal horizontal text alignment value.");
		}
        m_hImageAlign = halign;
    }

    /**
     * Get the vertical image alignment within the layout. One of
     * {@link Alignment#TOP}, {@link Alignment#BOTTOM}, or
     * {@link Alignment#CENTER}. The default is a centered image.
     * @return the vertical image alignment
     */
    public Alignment getVerticalImageAlignment() {
        return m_vImageAlign;
    }

    /**
     * Set the vertical image alignment within the layout. One of
     * {@link Alignment#TOP}, {@link Alignment#BOTTOM}, or
     * {@link Alignment#CENTER}. The default is a centered image.
     * @param valign the desired vertical image alignment
     */
    public void setVerticalImageAlignment(Alignment valign) {
        if ( valign != Alignment.TOP &&
             valign != Alignment.BOTTOM &&
             valign != Alignment.CENTER ) {
			throw new IllegalArgumentException(
                    "Illegal vertical text alignment value.");
		}
        m_vImageAlign = valign;
    }

    /**
     * Get the image position, determining where the image is placed with
     * respect to the text. The default is left.
     * @return the image position
     */
    public Alignment getImagePosition() {
        return m_imagePos;
    }

    /**
     * Set the image position, determining where the image is placed with
     * respect to the text. The default is left.
     * @param pos the desired image position
     */
    public void setImagePosition(Alignment pos) {
        m_imagePos = pos;
    }

    // ------------------------------------------------------------------------

    /**
     * Get the horizontal alignment of this node with respect to its
     * x, y coordinates.
     * @return the horizontal alignment, one of
     * {@link Alignment#LEFT}, {@link Alignment#RIGHT}, or
     * {@link Alignment#CENTER}.
     */
    public Alignment getHorizontalAlignment() {
        return m_xAlign;
    }

    /**
     * Get the vertical alignment of this node with respect to its
     * x, y coordinates.
     * @return the vertical alignment, one of
     * {@link Alignment#TOP}, {@link Alignment#BOTTOM}, or
     * {@link Alignment#CENTER}.
     */
    public Alignment getVerticalAlignment() {
        return m_yAlign;
    }

    /**
     * Set the horizontal alignment of this node with respect to its
     * x, y coordinates.
     * @param align the horizontal alignment, one of
     * {@link Alignment#LEFT}, {@link Alignment#RIGHT}, or
     * {@link Alignment#CENTER}.
     */
    public void setHorizontalAlignment(Alignment align) {
        m_xAlign = align;
    }

    /**
     * Set the vertical alignment of this node with respect to its
     * x, y coordinates.
     * @param align the vertical alignment, one of
     * {@link Alignment#TOP}, {@link Alignment#BOTTOM}, or
     * {@link Alignment#CENTER}.
     */
    public void setVerticalAlignment(Alignment align) {
        m_yAlign = align;
    }

    /**
     * Returns the amount of padding in pixels between the content
     * and the border of this item along the horizontal dimension.
     * @return the horizontal padding
     */
    public int getHorizontalPadding() {
        return m_horizBorder;
    }

    /**
     * Sets the amount of padding in pixels between the content
     * and the border of this item along the horizontal dimension.
     * @param xpad the horizontal padding to set
     */
    public void setHorizontalPadding(int xpad) {
        m_horizBorder = xpad;
    }

    /**
     * Returns the amount of padding in pixels between the content
     * and the border of this item along the vertical dimension.
     * @return the vertical padding
     */
    public int getVerticalPadding() {
        return m_vertBorder;
    }

    /**
     * Sets the amount of padding in pixels between the content
     * and the border of this item along the vertical dimension.
     * @param ypad the vertical padding
     */
    public void setVerticalPadding(int ypad) {
        m_vertBorder = ypad;
    }

    /**
     * Get the padding, in pixels, between an image and text.
     * @return the padding between an image and text
     */
    public int getImageTextPadding() {
        return m_imageMargin;
    }

    /**
     * Set the padding, in pixels, between an image and text.
     * @param pad the padding to use between an image and text
     */
    public void setImageTextPadding(int pad) {
        m_imageMargin = pad;
    }

    /**
     *
     * @return the maximum line length, or null if there is no maximum
     */
    public Integer getMaxLineLength() {
		return maxLineLength;
	}

    /**
     *
     * @param maxLineLength the maximum line length
     */
	public void setMaxLineLength(Integer maxLineLength) {
		this.maxLineLength = maxLineLength;
	}

} // end of class LabelRenderer
