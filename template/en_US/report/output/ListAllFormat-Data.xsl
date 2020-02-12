<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:output method="html" indent="no" />
	<xsl:strip-space elements="*" />
	<xsl:variable name="reportType">
		<xsl:value-of select="/root/@reportType" />
	</xsl:variable>
	<xsl:template match="root">

		<table width="100%" border="1" cellpadding="0" cellspacing="0">
			<colgroup>
				<col width="20%" />
				<col />
				<col width="10%" />
				<col width="10%" />
			</colgroup>
			<thead>
				<tr>
					<td>报表名字</td>
					<td>导出的文件版本</td>
					<td>状态</td>
					<td></td>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td colspan="4">
						<b>通用报表</b>
					</td>
				</tr>
				<xsl:apply-templates select="common/format" />
				<tr>
					<td colspan="4">
						<b>商品特定报表</b>
					</td>
				</tr>
				<xsl:apply-templates select="commodity/format" />
			</tbody>
		</table>

	</xsl:template>
	<xsl:template match="format">
		<tr valign="top">
			<td>
				<xsl:value-of select="@name" />
			</td>
			<td>
				<xsl:apply-templates select="output" />
			</td>
			<td class="status_{@status}">
				<xsl:value-of select="@status" />
			</td>
			<td>
				<button type="button" name="n" onclick="updateReport('{../@type}','{@name}')">生成最新报表</button>
				<button type="button" name="n" onclick="deleteReportFormat('{../@type}','{@name}')">删除格式</button>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="output">
		<li>
			<a href="/ePage?pg=DownloadOutputFile&amp;reportType={$reportType}&amp;format={../@name}&amp;file={@name}" target="_blank">
				<xsl:value-of select="@name" />
			</a>
			-
			<xsl:value-of select="@createdAt" />
		</li>
	</xsl:template>
</xsl:stylesheet>