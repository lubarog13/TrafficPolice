package org.traffic_police.utils;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

/**
 * Базовый класс для всех форм приложения.
 */
public class BaseForm extends JFrame
{
    /**
     * Заголовок приложения
     */
    public static String APP_TITLE = "Данные о правонарушениях";
    
    /**
     * Иконка приложения, загружаемая из ресурсов.
     */
    public static Image APP_ICON = null;

    static {
        try {
            APP_ICON = ImageIO.read(BaseForm.class.getClassLoader().getResource("logo.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Конструктор базовой формы.
     * Инициализирует окно с заданными параметрами: устанавливает размеры,
     * центрирует окно на экране, задает заголовок и иконку.
     *
     * @param width ширина окна в пикселях
     * @param height высота окна в пикселях
     * @param mainWindow если true, закрытие окна завершает приложение,
     *                   иначе окно просто закрывается
     */
    public BaseForm(int width, int height, boolean mainWindow)
    {
        if (mainWindow) {
            setDefaultCloseOperation(EXIT_ON_CLOSE);
        } else {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        }
        setMinimumSize(new Dimension(width, height));
        setLocation(
                Toolkit.getDefaultToolkit().getScreenSize().width / 2 - width / 2,
                Toolkit.getDefaultToolkit().getScreenSize().height / 2 - height / 2
        );

        setTitle(APP_TITLE);
        if(APP_ICON != null) {
            setIconImage(APP_ICON);
        }
    }
}
