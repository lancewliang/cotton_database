<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:output method="html" indent="no" />
	<xsl:strip-space elements="*" />
	<xsl:template match="root">


		<table width="100%" border="1" cellpadding="0" cellspacing="0">
			<colgroup>
				<col />
				<col width="10%" />
				<col width="10%" />
			</colgroup>
			<thead>
				<tr>
					<td>名称</td>
					<td>状态</td>
					<td></td>
				</tr>
			</thead>
			<tbody>
				<xsl:apply-templates select="AntInstruction" />
			</tbody>
		</table>
	</xsl:template>
	<xsl:template match="AntInstruction">
	<tr>
			<td  >
				<xsl:value-of select="@name" />
			</td><td  >
        <xsl:value-of select="@status" />
      </td>
			<td>
				<xsl:if test="@isWorking='FALSE'">
					<button type="button" name="n" onclick="doFeedByName('{@name}');">執行爬取</button>
				</xsl:if>
			</td>
		</tr>

	</xsl:template>

</xsl:stylesheet>