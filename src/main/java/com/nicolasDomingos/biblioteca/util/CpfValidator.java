package com.nicolasDomingos.biblioteca.util;

public class CpfValidator {

    public static boolean isValid(String cpf) {
        cpf = cpf.replaceAll("[^0-9]", "");

        if (cpf.length() != 11) return false;

        if (cpf.chars().distinct().count() == 1) return false;

        try {
            int fistDigit = calcDigit(cpf.substring(0, 9), 10);
            int secondDigit = calcDigit(cpf.substring(0, 9) + fistDigit, 11);

            return cpf.equals(cpf.substring(0, 9) + fistDigit + secondDigit);
        } catch (Exception e) {
            return false;
        }
    }

    private static int calcDigit(String base, int pesoInicial) {
        int soma = 0;
        for (int i = 0; i < base.length(); i++) {
            soma += Character.getNumericValue(base.charAt(i)) * (pesoInicial - i);
        }
        int resto = soma % 11;
        return (resto < 2) ? 0 : 11 - resto;
    }
}