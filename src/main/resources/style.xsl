<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
        xmlns:xsl=
                "http://www.w3.org/1999/XSL/Transform"
        version="1.0"
        >
    <xsl:output method="html"/>
    <xsl:template match="/">
        <html>
            <head><meta charset="utf-8"/></head>
            <body>
                <h2 align="center">The result of parsing <a href="https://auto.ru/catalog">auto.ru</a>:</h2><br/><br/>
                <xsl:apply-templates select="cars" mode="text" />


                <table border="1" align="center">
                    <tr bgcolor="#c0c0c0">
                        <th>Brand</th>
                        <th>Model</th>
                        <th>Generation</th>
                        <th>Years</th>
                    </tr>
                    <xsl:apply-templates select="cars" mode="table"/>
                </table>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="cars" mode="table">
        <xsl:for-each select="brand">
            <xsl:sort select="@name"/>

            <xsl:for-each select="model">
                <xsl:sort select="@name"/>

                <xsl:for-each select="generation">
                    <xsl:sort select="@name"/>

                    <tr>
                        <td align="center">
                            <a id="{../../@name}" href="{../../@href}">
                                <xsl:value-of select="../../@name"/>
                            </a>
                        </td>

                        <td align="center">
                            <a href="{../@href}">
                                <xsl:value-of select="../@name"/>
                            </a>
                        </td>

                        <td align="center">
                                <xsl:value-of select="@name"/>

                        </td>
                        <td align="center"><xsl:value-of select="@years"/></td>
                    </tr>
                </xsl:for-each>

            </xsl:for-each>

        </xsl:for-each>
    </xsl:template>

    <xsl:template match="cars" mode="text">
        <xsl:for-each select="brand">
            <xsl:sort select="@name"/>
            <a href="#{@name}">
                <xsl:value-of select="@name"/>
            </a> |
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>