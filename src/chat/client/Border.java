package chat.client;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Border extends Canvas implements CommonSettings {

    int mode,width,height, size, bsize;
    Dimension dim;
    BorderPanel parent;
    TapPanel Cframe;
    Color c1;
    int tabHeight;
    ChatClient chatclient;
    FontMetrics fontmetrics;

    Border(TapPanel tappanel, ChatClient app, BorderPanel borderpanel, int i, int j) {
        Cframe = tappanel;
        mode = i;
        size = j;
        chatclient = app;
        parent = borderpanel;
        bsize = 4;
        tabHeight = 22;
        if (i == 1) {
            height = 38;
            width = j;
        }
        if (i == 4) {
            height = bsize + 4;
            width = j;
        }
        if (i == 2 || i == 3) {
            width = bsize + 4;
            height = j;
        }
        if (i == 5) {
            width = j;
            height = 8;
        }
        dim = new Dimension(width, height);
        setSize(dim);//resize(dim);
        validate();
    }
    public void drawBottom(Graphics g) {
        int i = getSize().width;
        int j = getSize().height;
        g.setColor(SSTABBUTTON);
        g.fillRect(0, 0, i, j);
        g.setColor(Color.white);
        g.drawLine(1, 0, 1, j - 2);
        int k = i - (bsize + 4);
        g.drawLine(bsize + 2, 1, k, 1);
        g.fillRect(k, 0, 1, 1);
        g.setColor(Color.gray);
        g.drawLine(bsize + 2, 0, bsize + 2, 0);
        g.drawLine(1, bsize + 2, i - 2, bsize + 2);
        g.drawLine(i - 2, 0, i - 2, j - 2);
        g.setColor(SSTABBUTTON);
        g.drawLine(1, bsize + 3, i - 1, bsize + 3);
        g.drawLine(i - 1, 0, i - 1, j - 1);
    }

    @Override
    public void paint(Graphics g) {
        if (mode == 1) {
            drawTabs(g);
            return;
        }
        if (mode == 2) {
            drawVertical(g);
            return;
        }
        if (mode == 3) {
            drawVertical(g);
            return;
        }
        if (mode == 4) {
            drawBottom(g);
            return;
        } else {
            drawHorizontal(g);
            return;
        }
    }

    public void drawTab(Graphics g, int i, int j, int k, int l, boolean flag, String s) {
        g.setColor(SSTABBUTTON);
        g.fillRect(i, j, k, l);
        g.setColor(SSTABBUTTON);
        g.drawLine(i, j, (i + k) - 2, j);
        g.drawLine(i, j, i, (j + l) - 1);
        g.setColor(Color.gray);
        g.drawLine((i + k) - 2, j, (i + k) - 2, (j + l) - 1);
        g.setColor(BACKGROUND);
        g.drawLine((i + k) - 1, j + 1, (i + k) - 1, (j + l) - 1);
        g.setColor(LABEL_TEXT_COLOR);
        if (flag) {
            g.drawString(s, i + (((TAPPANEL_WIDTH / TAP_COUNT) - fontmetrics.stringWidth(s)) / 2), j + 16);
            return;
        } else {
            g.drawString(s, i + (((TAPPANEL_WIDTH / TAP_COUNT) - fontmetrics.stringWidth(s)) / 2), j + 16);
            return;
        }
    }

    public void setTab(int i) {
        parent.curTab = i;
        String s = parent.cardNames.elementAt(i);
        parent.cardLayout.show(parent.cardPanel, s);
        repaint();
    }

    public void drawTop(Graphics g) {
        int i = getSize().width;
        int j = getSize().height;
        g.setColor(BACKGROUND);
        g.fillRect(0, 0, getSize().width, getSize().height);
        g.setColor(BACKGROUND);
        g.drawLine(1, 1, 1, j + 1);
        g.drawLine(1, 1, i - 3, 1);
        g.setColor(BACKGROUND);
        g.drawLine(0, 0, i, 0);
        g.drawLine(0, 1, 0, j + 1);
        g.fillRect(2, 2, bsize, j);
        g.fillRect(bsize + 2, 2, i - bsize, bsize);
        g.setColor(Color.gray);
        g.drawLine(bsize + 2, bsize + 3, i - (bsize + 2), bsize + 3);
        g.drawLine(i - 2, 1, i - 2, j + 1);
        g.setColor(BACKGROUND);
        g.drawLine(bsize + 3, bsize + 4, i - (bsize + 3), bsize + 4);
        g.drawLine(i - 1, 1, i - 1, j + 1);
    }

    @Override
    public Dimension minimumSize() {
        return dim;
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    public void drawVertical(Graphics g) {
        int i = getSize().height;
        g.setColor(SSTABBUTTON);
        g.drawLine(0, 0, 0, i);
        g.fillRect(2, 0, bsize, i);
        g.setColor(SSTABBUTTON);
        g.drawLine(1, 0, 1, i);
        g.setColor(Color.gray);
        g.drawLine(bsize + 2, 0, bsize + 2, i);
        g.setColor(SSTABBUTTON);
        g.drawLine(bsize + 3, 0, bsize + 3, i);
    }

    @Override
    public boolean mouseEnter(Event event, int i, int j) {
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        return true;
    }

    @Override
    public boolean mouseExit(Event event, int i, int j) {
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        return true;
    }

    @Override
    public boolean mouseDown(Event event, int i, int j) {
        if (mode != 1) {
            return true;
        }
        for (int k = 0; k < parent.tabPos.size(); k++) {
            Rectangle rectangle = parent.tabPos.elementAt(k);
            if (rectangle.inside(i, j)) {
                setTab(k);
            }
        }

        return true;
    }

    @Override
    public Dimension preferredSize() {
        return minimumSize();
    }

    public void drawTabs(Graphics g) {
        int i = getSize().width;
        int k = getSize().height;
        int l = k - (bsize + 4);
        /**
         * ***Top Background********
         */
        g.setColor(BACKGROUND);
        g.fillRect(0, 0, i, k);
        g.setFont(parent.textFont);
        fontmetrics = g.getFontMetrics();
        fontmetrics.getHeight();
        int i1 = parent.xofs + 1;
        byte byte0 = 7;
        g.setColor(SSTABBUTTON);
        g.fillRect(0, l, i, k);
        g.drawLine(0, l, 0, k + 1);
        g.fillRect(2, l + 1, bsize, k);
        g.fillRect(bsize + 2, l + 1, i - bsize, bsize);
        g.setColor(SSTABBUTTON);
        g.drawLine(1, l, 1, k + 1);
        g.drawLine(1, l, i - 3, l);
        g.setColor(Color.gray);
        g.drawLine(bsize + 2, l + bsize + 2, i - (bsize + 2), l + bsize + 2);
        g.drawLine(i - 2, l, i - 2, k + 1);
        g.setColor(SSTABBUTTON);
        g.drawLine(bsize + 3, l + bsize + 3, i - (bsize + 3), l + bsize + 3);
        g.drawLine(i - 1, l, i - 1, k + 1);
        parent.tabPos.removeAllElements();
        for (int j1 = 0; j1 < parent.tabNames.size(); j1++) {
            String s = parent.tabNames.elementAt(j1);
            Rectangle rectangle1 = new Rectangle();
            int j = fontmetrics.stringWidth(s);
            rectangle1.x = i1;
            rectangle1.y = byte0 + 1;
            rectangle1.width = TAPPANEL_WIDTH / TAP_COUNT;
            rectangle1.height = tabHeight;
            parent.tabPos.addElement(rectangle1);
            drawTab(g, rectangle1.x, rectangle1.y, rectangle1.width, rectangle1.height, false, s);
            i1 += rectangle1.width;
        }

        Rectangle rectangle = parent.tabPos.elementAt(parent.curTab);
        drawTab(g, rectangle.x, rectangle.y - 4, rectangle.width + 2, rectangle.height + 5, true, parent.tabNames.elementAt(parent.curTab));
    }

    public void drawHorizontal(Graphics g) {
        g.setColor(SSTABBUTTON);
        g.fillRect(0, 0, width, height);
        g.setColor(SSTABBUTTON);
        int i = width - 8;
        g.drawLine(0, 1, i, 1);
        g.fillRect(i, 0, 1, 1);
        g.setColor(SSTABBUTTON);
        g.drawLine(6, 0, 6, 0);
        g.drawLine(1, 6, width - 2, 6);
        g.drawLine(width - 2, 0, width - 2, height - 2);
        g.setColor(SSTABBUTTON);
        g.drawLine(1, 7, width - 1, 7);
        g.drawLine(width - 1, 0, width - 1, height - 1);
    }

}
