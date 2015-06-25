/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.invation.code.toval.constraint.graphic;

import de.invation.code.toval.constraint.NumberOperator;
import de.invation.code.toval.constraint.StringOperator;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import static javax.swing.SwingConstants.CENTER;
import static javax.swing.SwingConstants.LEFT;

/**
 *
 * @author stocker
 */
public class ConstraintListRenderer extends JLabel implements ListCellRenderer {

        public ConstraintListRenderer() {
            setOpaque(true);
            setHorizontalAlignment(LEFT);
            setVerticalAlignment(CENTER);
            this.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            setText(value.toString());
            if (value instanceof NumberOperator || value instanceof StringOperator) {
//				setToolTipText(String.format(((NumberOperator) value).getStringFormat(), value.toString()));
                setToolTipText(((NumberOperator) value).getStringFormat());
            }

            return this;
        }

    }