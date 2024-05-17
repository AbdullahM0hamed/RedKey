package com.redkey.keyboard.emoji;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.OverScroller;
import com.redkey.keyboard.R;
import java.util.ArrayList;
import java.util.List;

public class EmojiHandler  {
    private static Paint paint = new Paint();
    public static OverScroller scroller = null;
    public static GestureDetector detector = null; 
    public static EmojiListener listener = null;
    private static String[] smileys = null; 
    private static String[] animals = null; 
    private static String[] food = null; 
    private static String[] transport; 
    private static String[] activities = null; 
    private static String[] objects = null; 
    private static String[] symbols = null; 
    private static String[] flags = null; 

    private static String[] clean(String[] dst, String[] src) { 
        if (dst != null) { 
            return dst;
        }
        List<String> cleanedList = new ArrayList<String>(); 
        for (String s : src) { 
            if (paint.hasGlyph(s) && paint.measureText(s) > 0)  {
                cleanedList.add(s);
            }
        }
        return cleanedList.toArray(new String[0]);
    } 

    private static String[] getEmojis() { 
        return getEmojis(selected);
    } 

    private static String[] getEmojis(int selected) { 
        switch (selected) { 
            case 1: 
                animals = clean(animals, Emojis.animals);
                return animals;
            case 2:
                food = clean(food, Emojis.food);
                return food; 
            case 3: 
                transport = clean(transport, Emojis.transport);
                return transport; 
            case 4: 
                activities = clean(activities, Emojis.activities);
                return activities; 
            case 5:
                objects = clean(objects, Emojis.objects);
                return objects; 
            case 6: 
                symbols = clean(symbols, Emojis.symbols);
                return symbols; 
            case 7:
                flags = clean(flags, Emojis.flags);
                return flags;
        }

        smileys = clean(smileys, Emojis.smileys);
        return smileys;
    } 

    private static Rect visibleRect = new Rect();
    private static void initRect(View view) {
        view.getLocalVisibleRect(visibleRect);
    } 

    private static int emojiHeight = 0; 
    private static int bottomHeight = 0;
    private static void setHeight(View view) { 
        if (emojiHeight == 0) { 
            paint.setTextSize(50);
            String emoji = getEmojis()[0]; 
            Rect emoteRect = new Rect(); 
            paint.getTextBounds(emoji, 0, emoji.length(), emoteRect);  
            emojiHeight = emoteRect.height();
            bottomHeight = (int)(emoteRect.height() * 1.5);
        }
    } 

    private static int rowHeight = 1;
    private static void drawEmojis(View view, Canvas canvas, int rowCount, int diff, int emojiWidth, int emojiTab, int offset, int yOffset) { 
        int emojiLength = getEmojis(emojiTab).length;
        int rows = (int) Math.ceil(emojiLength / (float) rowCount);
        for (int row = 0; row <= rows; row++) {
            for (int i = row * rowCount; i <= (row * rowCount) + rowCount; i++) { 
                if (i >= emojiLength) { 
                    break;
                }

                String emoji = getEmojis(emojiTab)[i];
                paint.getTextBounds(emoji, 0, emoji.length(), r); 
                diff = emojiWidth - r.width(); 
                int posInRow = i - (row * rowCount); 
                if (rowHeight == 1) { 
                    int y = (row * emojiHeight) + (row * (diff / 2)); 
                    int y2 = ((row + 1) * emojiHeight) + ((row + 1) * (diff / 2));
                    rowHeight = y2 - y;
                }
                canvas.drawText(emoji, (emojiWidth * posInRow) + (diff / 2) + offset, 50 + (row * emojiHeight) + (row * (diff / 2)) - yOffset, paint); 
                if (i == emojiLength - 1 && emojiTab == selected) { 
                    bottom = (50 + (row * emojiHeight) + (row * (diff / 2)) + emojiHeight + bottomHeight) - view.getHeight();
                }
            } 
        }
    } 

    private static int bottom = 0; 
    private static Paint white = new Paint(); 
    private static Paint semiTransparent = new Paint();
    private static Paint semiTransparentWhite = new Paint();
    private static Rect r = new Rect(); 
    private static boolean paintInitialised = false;
    public static void onDraw(View view, Canvas canvas) { 
        if (!paintInitialised) {
            white.setColor(0xFFFFFFFF);
            semiTransparent.setColor(0x55000000);
            semiTransparent.setTextSize(50);
            semiTransparentWhite.setTextSize(50);
            semiTransparentWhite.setColor(0xFFFFFFFF);
            semiTransparentWhite.setAlpha(150);
            paintInitialised = true;
        }
        initRect(view); 
        int rowCount = view.getWidth() / 100;
        int emojiWidth = view.getWidth() / rowCount; 
        setHeight(view); 
        canvas.save(); 
        int diff = 0; 
        canvas.clipRect(visibleRect.left, visibleRect.top, visibleRect.right - view.getScrollX(), visibleRect.bottom - bottomHeight);  
        drawEmojis(view, canvas, rowCount, diff, emojiWidth, selected, 0, 0); 
        if (view.getScrollX() != 0) { 
            canvas.restore(); 
            canvas.save(); 
            canvas.clipRect(visibleRect.left, visibleRect.top, visibleRect.right, visibleRect.bottom - bottomHeight);
            int next = selected + (view.getScrollX() > 0 ? 1 : -1); 
            int offset = next > selected ? visibleRect.right - view.getScrollX() : -(visibleRect.right - (view.getScrollX() - emojiWidth));
            drawEmojis(view, canvas, rowCount, diff, emojiWidth, next, offset, (int)-scrollY);
        }

        float scrollTop = visibleRect.top + (scrollY / bottom) * (visibleRect.height() - bottomHeight - emojiHeight);
        canvas.drawRoundRect( 
                (float)(visibleRect.right - (emojiWidth / 10)), 
                scrollTop,
                visibleRect.right, 
                scrollTop + emojiHeight, 
                10,
                10,
                white	
                );
        canvas.restore(); 
        canvas.drawRect(visibleRect.left, visibleRect.bottom - bottomHeight, visibleRect.right, visibleRect.bottom, semiTransparent); 
        float stickyWidth = visibleRect.width() / rowCount; 
        if (scrollX < 0) { 
            scrollX = 0;
        } 

        int bottomDiff = bottomHeight - emojiHeight; 
        int bottomWidth = ((tabIconsOutline.length) * bottomHeight) - bottomDiff;
        if ((scrollX > (visibleRect.left + (2 * stickyWidth) + bottomWidth - visibleRect.right)) && scrollX != 0) { 
            scrollX = visibleRect.left + (2 * stickyWidth) + bottomWidth - visibleRect.right;
        } 

        if (visibleRect.left + bottomWidth <= visibleRect.right - (2 * stickyWidth)) { 
            scrollX = 0;
        }

        canvas.save(); 
        canvas.clipRect(visibleRect.left + stickyWidth, visibleRect.bottom - bottomHeight, visibleRect.right - stickyWidth, visibleRect.bottom);
        drawTabs(view, canvas, emojiWidth); 
        diff = emojiWidth - r.width();
        Drawable backspace = view.getResources().getDrawable(R.drawable.ic_backspace);
        Rect bounds = new Rect(
                (int)(visibleRect.right - stickyWidth) + (diff / 2),
                (int)visibleRect.bottom - bottomHeight + (diff / 2),
                (int)visibleRect.right - (diff / 2),
                (int)visibleRect.bottom - (diff / 2)
                ); 

        canvas.restore(); 
        backspace.setAlpha(150);
        backspace.setBounds(bounds); 
        backspace.draw(canvas); 
        String abc = "abc"; 
        Rect measureWhite = new Rect();
        semiTransparentWhite.getTextBounds(abc, 0, abc.length(), measureWhite); 
        int heightDiff = bottomHeight - measureWhite.height();
        canvas.drawText(abc, visibleRect.left + (diff / 2), visibleRect.bottom - (heightDiff / 2), semiTransparentWhite);
    }

    public static float scrollY = 0; 
    public static float scrollX = 0;
    private static View v = null; 
    private static View getView() { 
        return v;
    } 

    private static Rect getVisible() { 
        return visibleRect;
    }
    public static Boolean onTouchEvent(View view, MotionEvent event) { 
        v = view;
        if (detector == null) { 
            scroller = new OverScroller(view.getContext());
            detector = new GestureDetector(view.getContext(), new GestureDetector.SimpleOnGestureListener() { 
                @Override 
                public boolean onScroll(MotionEvent e, MotionEvent e2, float distX, float distY) { 
                    if (e == null | e2 == null) { return true; }
                    if (e.getY() >= visibleRect.bottom - bottomHeight - scrollY) { 
                        scrollX += distX; 
                        getView().invalidate();
                        return true;
                    } 

                    float diffX = Math.abs(distX - 0); 
                    float diffY = Math.abs(distY - 0);
                    if (diffX > diffY && !(selected == 0 && distX < 0) && !(selected == 7 && distX > 0)) {
                        getView().scrollBy((int)distX, 0); 
                        return true;
                    } 

                    if (getView().getScrollX() != 0) { 
                        return true;
                    } 

                    float valDistY = distY; 
                    if (scrollY >= 0 && scrollY <= bottom) { 
                        if (scrollY + valDistY < 0) { 
                            valDistY = -scrollY;
                        } 

                        if (scrollY + valDistY > bottom) { 
                            valDistY = bottom - scrollY;
                        }
                        getView().scrollBy(0, (int) valDistY); 
                        scrollY += valDistY; 
                    }
                    return true;
                } 

                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) { 
                    float y = e.getY(); 
                    Rect visible = getVisible(); 
                    if (y > getView().getHeight() - bottomHeight) { 
                        float x = e.getX();
                        float width = visible.width() / (getView().getWidth() / 100); 
                        float offset = 0; 
                        int bottomWidth = ((tabIconsOutline.length + 1) * bottomHeight) - (bottomHeight / emojiHeight);
                        if (bottomWidth + visible.left <= visible.right - (2 * width)) { 
                            offset = visible.right - (2 * width) - bottomWidth - visible.left;
                        }

                        if (x > visible.left + width && x < visible.right - width) { 
                            int i = (int)((x + scrollX - (offset / 2)) / bottomHeight) - 1; 
                            selected = i; 
                            getView().invalidate(); 
                            scrollY = 0;
                            getView().scrollTo(0, 0);
                        } else { 
                            listener.onButtonClicked(x >= visibleRect.right - width);
                        }
                    } else {
                        int emojiWidth = getView().getWidth() / (getView().getWidth() / 100);
                        int line = ((int)(scrollY + e.getY() + 50 + ((emojiWidth - r.width()) / 2)) / rowHeight) - 1; 
                        if (line < 0) { 
                            line = 0;
                        } 

                        int pos = (int)((e.getX() + ((emojiWidth - r.width()) / 2)) / emojiWidth); 
                        int perRow = (getView().getWidth() + ((emojiWidth - r.width()) / 2)) / emojiWidth; 
                        int index = (perRow * line) + pos; 
                        if (listener != null && index < getEmojis().length && index > -1) {
                            listener.onEmojiClicked(getEmojis()[index]);
                        }
                    }
                    return true;
                }

                @Override
                public boolean onFling(MotionEvent e, MotionEvent e2, float velX, float velY) { 
                    if (e == null || e2 == null) { return true; }
                    if (e.getY() > visibleRect.bottom - bottomHeight) { 
                        int bottomDiff = bottomHeight - emojiHeight; 
                        int bottomWidth = ((tabIconsOutline.length) * bottomHeight) - bottomDiff; 
                        scroller.fling( 
                                (int)scrollX,
                                0,
                                (int)-velX,
                                0,
                                0,
                                visibleRect.left + (2 * (visibleRect.width() / (visibleRect.width() / 100))) + bottomWidth - visibleRect.right,
                                0,
                                0
                                );
                        return true;
                    }
                    scroller.fling(
                            0,
                            (int)scrollY,
                            0,
                            (int)-velY,
                            0,
                            0,
                            0,
                            bottom
                            ); 
                    getView().invalidate();
                    return true;
                }

                @Override
                public boolean onDown(MotionEvent e) { 
                    return true;
                }
            }); 
        } 

        if (event.getAction() == MotionEvent.ACTION_UP) { 
            int next = view.getScrollX() > 0 ? selected + 1 : selected - 1; 
            if (view.getScrollX() > (visibleRect.width() / 2) || -view.getScrollX() > (visibleRect.width() / 2)) { 
                int remaining = visibleRect.width() - view.getScrollX(); 
                boolean subtract = false;
                if (remaining > visibleRect.right) { 
                    remaining -= visibleRect.right;
                } 
                view.scrollBy(subtract ? -remaining : remaining, 0); 
                selected = next; 
                scrollY = 0;
                view.invalidate();
            }
            view.scrollBy(-view.getScrollX(), 0); 
        }
        return detector.onTouchEvent(event);
    } 

    private static int[] tabIconsOutline = new int[] { 
        R.drawable.ic_smile_outline_24,
            R.drawable.ic_animal_outline_24, 
            R.drawable.ic_fruit_outline_24, 
            R.drawable.ic_car_outline_24, 
            R.drawable.ic_ball_outline_24, 
            R.drawable.ic_book_outline_24, 
            R.drawable.ic_shape_outline_24, 
            R.drawable.ic_flag_outline_24
    }; 

    private static int[] tabIconsFill = new int[] { 
        R.drawable.ic_smile_24,
            R.drawable.ic_animal_24, 
            R.drawable.ic_fruit_24, 
            R.drawable.ic_car_24, 
            R.drawable.ic_ball_24, 
            R.drawable.ic_book_24, 
            R.drawable.ic_shape_24, 
            R.drawable.ic_flag_24
    }; 

    private static int selected = 0;
    private static void drawTabs(View view, Canvas canvas, float width) {
        int diff = bottomHeight - emojiHeight; 
        int bottomWidth = ((tabIconsOutline.length + 1) * bottomHeight) - diff; 
        float offset = 0; 
        if (bottomWidth + visibleRect.left <= visibleRect.right - (2 * width)) { 
            offset = visibleRect.right - (2 * width) - bottomWidth - visibleRect.left;
        }
        int baseLeft = visibleRect.left + (int)(width) - (int)(scrollX) + (int)(offset / 2);

        for (int i = 0; i < tabIconsOutline.length; i++) { 
            Rect iconBounds = new Rect(
                    baseLeft + (bottomHeight * i), 
                    visibleRect.bottom - bottomHeight + (diff / 2), 
                    baseLeft + ((i + 1) * bottomHeight) - diff, 
                    visibleRect.bottom - (diff / 2)
                    );  
            Drawable drawable = null; 
            int newSelected = selected; 
            if (view.getScrollX() > visibleRect.width() / 2) { 
                newSelected++;
            } 

            if (-view.getScrollX() > visibleRect.width() / 2) { 
                newSelected--;
            } 

            drawable = view.getContext().getResources().getDrawable(tabIconsOutline[i]);
            if (selected == i) { 
                int end = baseLeft + (bottomHeight * (i + 1)); 
                int scrollable = end - iconBounds.left; 
                int scrollDivider = visibleRect.width() / scrollable;
                canvas.drawRoundRect( 
                        iconBounds.left + (view.getScrollX() / scrollDivider), 
                        visibleRect.bottom - (emojiHeight / 10), 
                        iconBounds.right + (view.getScrollX() / scrollDivider),
                        visibleRect.bottom, 
                        10, 
                        10, 
                        semiTransparentWhite
                        );
            }

            if (newSelected == i) { 
                drawable = view.getContext().getResources().getDrawable(tabIconsFill[i]);
            }
            drawable.setBounds(iconBounds); 
            drawable.setAlpha(150); 
            drawable.draw(canvas);
        }
    } 

    public interface EmojiListener { 
        void onEmojiClicked(String emoji);
        void onButtonClicked(boolean deleteKey);
    }
}
