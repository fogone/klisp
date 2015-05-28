package ru.nobirds.klisp.token

import java.io.Writer
import java.math.BigDecimal

public class Decimal(override val value:BigDecimal) : Value<BigDecimal>
