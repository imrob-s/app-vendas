
package dev.imrob.appvendas.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * Classe utilitária com métodos auxiliares para manipulação de dados.
 * 
 * @author Robson Silva
 */
public class Util {
    
    /**
     * Converte um objeto para BigDecimal.
     *
     * <p>Este método tenta converter um objeto de diferentes tipos para BigDecimal. 
     * Suporta os seguintes tipos:</p>
     * 
     * <ul>
     *  <li>BigDecimal: Retorna o próprio objeto.</li>
     *  <li>Number: Converte o valor numérico para BigDecimal.</li>
     *  <li>String: Tenta analisar a string como um número decimal, 
     *  considerando a localização pt-BR para formatação de números. 
     *  Símbolos de moeda e espaços extras são removidos antes da análise.</li>
     * </ul>
     *
     * <p>Se o objeto não puder ser convertido, retorna null.</p>
     *
     * @param obj O objeto a ser convertido para BigDecimal.
     * @return O valor BigDecimal convertido ou null se a conversão falhar.
     */
    public static BigDecimal toBigDecimal(Object obj) {
        if (obj == null) {
            return null;
        }

        if (obj instanceof BigDecimal) {
            return (BigDecimal) obj;
        }

        if (obj instanceof Number) {
            return BigDecimal.valueOf(((Number) obj).doubleValue());
        }

        if (obj instanceof String) {
            String str = (String) obj;
            str = str.replaceAll("[^\\d,.-]", "").trim();

            DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(new Locale("pt", "BR"));
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("pt", "BR"));
            symbols.setDecimalSeparator(',');
            symbols.setGroupingSeparator('.');
            df.setParseBigDecimal(true);
            df.setDecimalFormatSymbols(symbols);

            try {
                return (BigDecimal) df.parse(str);
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
    
    /**
     * Formata um valor BigDecimal como moeda brasileira (Real).
     *
     * @param value O valor BigDecimal a ser formatado.
     * @return O valor formatado como moeda brasileira (Real) ou null se o valor for null.
     */
    public static String toReal(BigDecimal value) {
        if (value == null) {
            return null;
        }
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return currencyFormat.format(value);
}
}
