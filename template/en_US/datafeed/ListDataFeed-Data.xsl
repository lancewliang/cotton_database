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
					<td>导入文件</td>
					<td>状态</td>
					<td></td>
				</tr>
			</thead>
			<tbody>
				<xsl:apply-templates select="type" />
			</tbody>
		</table>
	</xsl:template>
	<xsl:template match="type">
		<tr>
			<td colspan="2">
				<xsl:value-of select="@name" />
			</td>
			<td>
				<button type="button" name="n" onclick="doFeedByType('{@name}');">按大类导入</button>
				<button type="button" name="n" onclick="doUploadFile('{@name}');">上传数据文件</button>
			</td>
		</tr>
		<xsl:apply-templates select="file" />
	</xsl:template>
	<xsl:template match="file">
		<tr>
			<td>
				--<xsl:value-of select="@name" />
			</td>
			<td class="status_{@status}">
				<xsl:value-of select="@status" />
			</td>
			<td>
				<button type="button" name="n" onclick="doFeedByFile('{../@name}','{@name}');">按文件导入</button>
			</td>
		</tr>
	</xsl:template>
</xsl:stylesheet>