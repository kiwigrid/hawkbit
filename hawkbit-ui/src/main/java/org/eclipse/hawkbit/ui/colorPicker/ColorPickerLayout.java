package org.eclipse.hawkbit.ui.colorPicker;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.hawkbit.ui.common.CoordinatesToColor;
import org.eclipse.hawkbit.ui.management.tag.SpColorPickerPreview;

import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.ui.AbstractColorPicker.Coordinates2Color;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Slider;
import com.vaadin.ui.components.colorpicker.ColorPickerGradient;
import com.vaadin.ui.components.colorpicker.ColorSelector;

public class ColorPickerLayout extends GridLayout {

    private static final long serialVersionUID = -7025970080613796692L;

    /**
     * Local Instance of ColorPickerPreview.
     */
    private SpColorPickerPreview selPreview;

    private ColorPickerGradient colorSelect;
    private Set<ColorSelector> selectors;
    private Color selectedColor;

    /** RGB color converter. */
    private final Coordinates2Color rgbConverter = new CoordinatesToColor();

    private Slider redSlider;
    private Slider greenSlider;
    private Slider blueSlider;

    public ColorPickerLayout() {

        setColumns(2);
        setRows(4);

        init();

        setStyleName("rgb-vertical-layout");

        addComponent(redSlider, 0, 1);
        addComponent(greenSlider, 0, 2);
        addComponent(blueSlider, 0, 3);

        addComponent(selPreview, 1, 0);
        addComponent(colorSelect, 1, 1, 1, 3);
    }

    public void init() {

        selectors = new HashSet<>();
        selectedColor = getDefaultColor();
        selPreview = new SpColorPickerPreview(selectedColor);

        colorSelect = new ColorPickerGradient("rgb-gradient", rgbConverter);
        colorSelect.setColor(selectedColor);
        colorSelect.setWidth("220px");

        redSlider = createRGBSlider("", "red");
        greenSlider = createRGBSlider("", "green");
        blueSlider = createRGBSlider("", "blue");

        selectors.add(colorSelect);
    }

    public Slider createRGBSlider(final String caption, final String styleName) {
        final Slider slider = new Slider(caption, 0, 255);
        slider.setImmediate(true);
        slider.setWidth("150px");
        slider.addStyleName(styleName);
        return slider;
    }

    public SpColorPickerPreview getSelPreview() {
        return selPreview;
    }

    public void setSelPreview(final SpColorPickerPreview selPreview) {
        this.selPreview = selPreview;
    }

    public ColorPickerGradient getColorSelect() {
        return colorSelect;
    }

    public void setColorSelect(final ColorPickerGradient colorSelect) {
        this.colorSelect = colorSelect;
    }

    public Set<ColorSelector> getSelectors() {
        return selectors;
    }

    public Color getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(final Color selectedColor) {
        this.selectedColor = selectedColor;
    }

    public Coordinates2Color getRgbConverter() {
        return rgbConverter;
    }

    public Color getDefaultColor() {
        return new Color(44, 151, 32);
    }

    public Slider getRedSlider() {
        return redSlider;
    }

    public void setRedSlider(final Slider redSlider) {
        this.redSlider = redSlider;
    }

    public Slider getGreenSlider() {
        return greenSlider;
    }

    public void setGreenSlider(final Slider greenSlider) {
        this.greenSlider = greenSlider;
    }

    public Slider getBlueSlider() {
        return blueSlider;
    }

    public void setBlueSlider(final Slider blueSlider) {
        this.blueSlider = blueSlider;
    }

}
