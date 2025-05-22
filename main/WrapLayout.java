package main;

import java.awt.*;
import javax.swing.*;

/**
 * WrapLayout: A custom FlowLayout that's better for JScrollPanes.
 * It makes components wrap to the next line if they don't fit,
 * and it tells the JScrollPane the correct total height needed.
 */
public class WrapLayout extends FlowLayout {

    // Constructors: Just call the original FlowLayout constructors.
    // We're only changing how the size is calculated, not the basic setup.
    public WrapLayout() {
        super();
    }

    public WrapLayout(int align) {
        super(align);
    }

    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    /**
     * This is called when Swing wants to know the "ideal" size of our container.
     * We calculate it by figuring out how components will wrap.
     */
    @Override
    public Dimension preferredLayoutSize(Container target) {
        // Use our custom calculation for the preferred size.
        return calculateLayoutSize(target, true);
    }

    /**
     * This is called when Swing wants to know the "smallest possible" size.
     * We also use our custom calculation, with a small adjustment typical for FlowLayout.
     */
    @Override
    public Dimension minimumLayoutSize(Container target) {
        Dimension minimum = calculateLayoutSize(target, false);
        // A small tweak FlowLayout often does for minimum width.
        minimum.width -= (getHgap() + 1);
        return minimum;
    }

    /**
     * The main logic: Figures out the container's width and height after wrapping.
     * 'preferred' is true if we want the ideal size, false for the smallest size.
     */
    private Dimension calculateLayoutSize(Container targetContainer, boolean preferredSizeWanted) {
        // How wide is the container we're laying out?
        int availableWidth = targetContainer.getWidth();

        // If our container is inside a JScrollPane, the JScrollPane's viewport
        // dictates the true available width for wrapping.
        Container parent = targetContainer.getParent();
        if (parent instanceof JViewport) {
            int viewportWidth = parent.getWidth();
            // Only use viewport width if it's a valid, positive number.
            if (viewportWidth > 0) {
                availableWidth = viewportWidth;
            }
        }

        // If we don't know the width yet (e.g., panel not shown),
        // assume it's infinitely wide for now. It'll get re-calculated later.
        if (availableWidth == 0) {
            availableWidth = Integer.MAX_VALUE;
        }

        // Get gaps (space between components) and insets (padding inside container).
        int hgap = getHgap();
        int vgap = getVgap();
        Insets insets = targetContainer.getInsets();

        // Calculate how much width is actually usable for components on each line,
        // after subtracting padding and space for gaps.
        int paddingAndGapsWidth = insets.left + insets.right + (hgap * 2);
        int usableWidthPerLine = availableWidth - paddingAndGapsWidth;

        // These will track the overall dimensions as we build lines.
        Dimension totalCalculatedSize = new Dimension(0, 0);
        int currentLineWidth = 0;  // How wide is the line we're currently building?
        int currentLineHeight = 0; // How tall is the line we're currently building? (tallest item)

        // Go through each component in our container.
        int numberOfComponents = targetContainer.getComponentCount();
        for (int i = 0; i < numberOfComponents; i++) {
            Component component = targetContainer.getComponent(i);
            // Only consider visible components.
            if (component.isVisible()) {
                // Get the component's own size (either its ideal or smallest).
                Dimension componentSize = preferredSizeWanted ?
                                          component.getPreferredSize() :
                                          component.getMinimumSize();

                // Will adding this component make the current line too wide?
                // (And make sure the line isn't empty before deciding to wrap).
                boolean needsToWrap = (currentLineWidth + componentSize.width > usableWidthPerLine) &&
                                      (currentLineWidth > 0);

                if (needsToWrap) {
                    // Yes, line is full. Finalize this line's contribution to total size.
                    addLineToTotal(totalCalculatedSize, currentLineWidth, currentLineHeight);
                    // Reset for the new line.
                    currentLineWidth = 0;
                    currentLineHeight = 0;
                }

                // If this isn't the first item on the line, add a horizontal gap.
                if (currentLineWidth != 0) {
                    currentLineWidth += hgap;
                }

                // Add the component to the current line.
                currentLineWidth += componentSize.width;
                currentLineHeight = Math.max(currentLineHeight, componentSize.height);
            }
        }
        // Don't forget to add the very last line we were building.
        addLineToTotal(totalCalculatedSize, currentLineWidth, currentLineHeight);

        // Now, add the container's own padding and overall gaps to the calculated size.
        totalCalculatedSize.width += paddingAndGapsWidth;
        totalCalculatedSize.height += insets.top + insets.bottom + (vgap * 2);

        // IMPORTANT for JScrollPane: If we had a real width to work with (not infinite),
        // then tell the JScrollPane that our preferred width IS that real width.
        // This makes the content fill the JScrollPane horizontally and scroll vertically.
        if (availableWidth != Integer.MAX_VALUE) {
            totalCalculatedSize.width = availableWidth;
        }

        return totalCalculatedSize;
    }

    /**
     * Helper: Updates the total container size with the size of a completed line.
     */
    private void addLineToTotal(Dimension totalSizeSoFar, int completedLineWidth, int completedLineHeight) {
        // The container's total width is the width of its widest line.
        totalSizeSoFar.width = Math.max(totalSizeSoFar.width, completedLineWidth);

        // If we've already added lines, add a vertical gap before this new line.
        if (totalSizeSoFar.height > 0) {
            totalSizeSoFar.height += getVgap();
        }
        // Add the height of this completed line.
        totalSizeSoFar.height += completedLineHeight;
    }
}