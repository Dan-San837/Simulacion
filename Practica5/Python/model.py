"""Modelo del generador congruencial lineal."""

from math import gcd

MODULO_POR_DEFECTO = 2 ** 32


class GeneradorCongruencialLineal:
    """Genera una secuencia mediante X(n+1) = (a * X(n) + c) mod m."""

    def __init__(self, a=1664525, c=1013904223, m=MODULO_POR_DEFECTO, semilla=12345):
        if m <= 0:
            raise ValueError("El modulo m debe ser positivo.")
        self.a = a
        self.c = c
        self.m = m
        self.semilla = semilla % m
        self.estado = self.semilla
        self.generados = 0

    def reiniciar(self):
        self.estado = self.semilla
        self.generados = 0

    def siguiente(self):
        self.estado = (self.a * self.estado + self.c) % self.m
        self.generados += 1
        return self.estado

    def siguiente_normalizado(self):
        return self.siguiente() / self.m

    def generar(self, cantidad):
        if cantidad < 0:
            raise ValueError("La cantidad debe ser no negativa.")
        return [self.siguiente() for _ in range(cantidad)]

    def normalizar(self, estados):
        return [estado / self.m for estado in estados]

    def parametros(self):
        return {
            "a": self.a,
            "c": self.c,
            "m": self.m,
            "semilla": self.semilla,
        }

    def analizar_periodo(self, estados):
        factores = self._factores_primos(self.m)
        condiciones = {
            "mcd(c, m) = 1": gcd(self.c, self.m) == 1,
            "(a - 1) divisible por los factores primos de m": all(
                (self.a - 1) % factor == 0 for factor in factores
            ),
            "(a - 1) divisible por 4 cuando m es multiplo de 4": (
                self.m % 4 != 0 or (self.a - 1) % 4 == 0
            ),
        }
        periodo_completo = all(condiciones.values())
        return {
            "factores_primos_m": factores,
            "condiciones_cumplidas": condiciones,
            "periodo_completo": periodo_completo,
            "periodo_teorico": self.m if periodo_completo else None,
            "muestra": len(estados),
            "repetidos_en_muestra": len(estados) != len(set(estados)),
        }

    @staticmethod
    def _factores_primos(numero):
        factores = []
        n = numero
        divisor = 2
        while divisor * divisor <= n:
            if n % divisor == 0:
                factores.append(divisor)
                while n % divisor == 0:
                    n //= divisor
            divisor += 1
        if n > 1:
            factores.append(n)
        return factores
