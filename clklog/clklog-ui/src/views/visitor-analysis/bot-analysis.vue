<template>
  <div class="bot-analysis-page">
    <div class="bot-filter-bar">
      <div class="radio-com">
        <span class="radio-com-label">时间：</span>
        <el-radio-group v-model="timeFlag" class="radio-com-el" @change="handleTimeChange">
          <el-radio label="today">今日</el-radio>
          <el-radio label="week">本周</el-radio>
          <el-radio label="month">本月</el-radio>
        </el-radio-group>
      </div>
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        size="small"
        unlink-panels
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="yyyy-MM-dd"
        :clearable="false"
        :picker-options="pickerOptions"
        class="filter-date"
        @change="handleDateChange"
      />
    </div>
    <div class="public-block">
      <!-- 流量概览 -->
      <div class="block-line">
        <div class="block-main public-hoverItem overview-card">
          <div class="block-head" style="margin-bottom: 0px">
            <div class="block-title">流量概览</div>
          </div>
          <div class="bid-list-page">
            <div class="bid-list-header">
              <div class="header-name w156"></div>
              <div class="header-name w156">
                浏览量(PV)
                <div class="parentPosition">
                  <el-popover placement="bottom-start" width="200" trigger="hover" class="popImg">
                    <div v-html="$sharedText.pv" style="padding: 5px; font-size: 12px"></div>
                    <img slot="reference" class="icon-question" src="@/assets/images/question.png" alt="" />
                  </el-popover>
                </div>
              </div>
              <div class="header-name w156">
                访问次数
                <div class="parentPosition">
                  <el-popover placement="bottom-start" width="200" trigger="hover" class="popImg">
                    <div v-html="$sharedText.visitCount" style="padding: 5px; font-size: 12px"></div>
                    <img slot="reference" class="icon-question" src="@/assets/images/question.png" alt="" />
                  </el-popover>
                </div>
              </div>
              <div class="header-name w156">
                访客数
                <div class="parentPosition">
                  <el-popover placement="bottom-start" width="200" trigger="hover" class="popImg">
                    <div v-html="$sharedText.uv" style="padding: 5px; font-size: 12px"></div>
                    <img slot="reference" class="icon-question" src="@/assets/images/question.png" alt="" />
                  </el-popover>
                </div>
              </div>
              <div class="header-name w156">
                IP数
                <div class="parentPosition">
                  <el-popover placement="bottom-start" width="200" trigger="hover" class="popImg">
                    <div v-html="$sharedText.ip" style="padding: 5px; font-size: 12px"></div>
                    <img slot="reference" class="icon-question" src="@/assets/images/question.png" alt="" />
                  </el-popover>
                </div>
              </div>
              <div class="header-name w156">
                平均访问页数
                <div class="parentPosition">
                  <el-popover placement="bottom-start" width="200" trigger="hover" class="popImg">
                    <div v-html="$sharedText.avgPv" style="padding: 5px; font-size: 12px"></div>
                    <img slot="reference" class="icon-question" src="@/assets/images/question.png" alt="" />
                  </el-popover>
                </div>
              </div>
              <div class="header-name w156">
                平均访问时长
                <div class="parentPosition">
                  <el-popover placement="bottom-start" width="200" trigger="hover" class="popImg">
                    <div v-html="$sharedText.avgVisitTime" style="padding: 5px; font-size: 12px"></div>
                    <img slot="reference" class="icon-question" src="@/assets/images/question.png" alt="" />
                  </el-popover>
                </div>
              </div>
            </div>
            <div class="bid-list-record">
              <div class="bid-list-item1 w157"><p>机器人</p></div>
              <div class="bid-list-item w158"><p>{{ botRow.pv || "--" }}</p></div>
              <div class="bid-list-item w158"><p>{{ botRow.visitCount || "--" }}</p></div>
              <div class="bid-list-item w158"><p>{{ botRow.uv || "--" }}</p></div>
              <div class="bid-list-item w158"><p>{{ botRow.ipCount || "--" }}</p></div>
              <div class="bid-list-item w158"><p>{{ averageRulesEvent(botRow.avgPv) || "--" }}</p></div>
              <div class="bid-list-item w158"><p>{{ formatTimeFun(botRow.avgVisitTime) || "--" }}</p></div>
            </div>
            <div class="bid-list-record">
              <div class="bid-list-item1 w157"><p>真人</p></div>
              <div class="bid-list-item w159"><p>{{ humanRow.pv || "--" }}</p></div>
              <div class="bid-list-item w159"><p>{{ humanRow.visitCount || "--" }}</p></div>
              <div class="bid-list-item w159"><p>{{ humanRow.uv || "--" }}</p></div>
              <div class="bid-list-item w159"><p>{{ humanRow.ipCount || "--" }}</p></div>
              <div class="bid-list-item w159"><p>{{ averageRulesEvent(humanRow.avgPv) || "--" }}</p></div>
              <div class="bid-list-item w159"><p>{{ formatTimeFun(humanRow.avgVisitTime) || "--" }}</p></div>
            </div>
          </div>
        </div>
      </div>

      <!-- 机器人分类 / 地域分布 -->
      <div class="block-line">
        <div class="block-main public-hoverItem left chart-card">
          <div class="block-head">
            <div class="block-title">机器人分类</div>
            <el-radio-group v-model="categoryDisplayMode" size="mini" class="area-mode-switch">
              <el-radio-button label="pie">饼图</el-radio-button>
              <el-radio-button label="list">列表</el-radio-button>
            </el-radio-group>
          </div>
          <div v-show="categoryDisplayMode === 'pie'" ref="categoryChart" class="chart-box" />
          <el-table
            v-show="categoryDisplayMode === 'list'"
            :data="categoryTableItems"
            size="small"
            border
            height="320"
            class="area-list-table"
            :header-cell-style="{ textAlign: 'center', background: '#f7fafe' }"
          >
            <el-table-column type="index" label="排序" width="70" align="center" />
            <el-table-column prop="categoryName" label="分类" :sortable="true" align="center" />
            <el-table-column prop="ipCount" label="IP数" :sortable="true" align="center" />
            <el-table-column prop="rateText" label="占比" :sortable="true" align="center" :sort-by="'ipCount'" />
          </el-table>
        </div>
        <div class="block-main public-hoverItem right chart-card">
          <div class="block-head">
            <div class="block-title">地域分布</div>
            <el-radio-group v-model="areaDisplayMode" size="mini" class="area-mode-switch">
              <el-radio-button label="bar">柱状图</el-radio-button>
              <el-radio-button label="list">列表</el-radio-button>
            </el-radio-group>
          </div>
          <div v-show="areaDisplayMode === 'bar'" ref="barChart" class="chart-box" />
          <el-table
            v-show="areaDisplayMode === 'list'"
            :data="areaRegionItems"
            size="small"
            border
            height="320"
            class="area-list-table"
            :header-cell-style="{ textAlign: 'center', background: '#f7fafe' }"
          >
            <el-table-column type="index" label="排序" width="70" align="center" />
            <el-table-column prop="name" label="地域" :sortable="true" align="center" />
            <el-table-column prop="ipCount" label="IP数" :sortable="true" align="center" />
            <el-table-column prop="rateText" label="占比" :sortable="true" align="center" :sort-by="'ipCount'" />
          </el-table>
        </div>
      </div>

      <!-- 搜索引擎/AI爬虫 抓取页面数 vs IP数 -->
      <div class="block-line">
        <div class="block-main public-hoverItem left chart-card">
          <div class="block-head">
            <div class="block-title">搜索引擎 抓取页面数 vs IP数</div>
            <el-radio-group v-model="engineDisplayMode" size="mini" class="area-mode-switch">
              <el-radio-button label="bar">条形图</el-radio-button>
              <el-radio-button label="list">列表</el-radio-button>
            </el-radio-group>
          </div>
          <div v-show="engineDisplayMode === 'bar'" ref="engineChart" class="chart-box" />
          <el-table
            v-show="engineDisplayMode === 'list'"
            :data="engineTableItems"
            size="small"
            border
            height="320"
            class="area-list-table"
            :header-cell-style="{ textAlign: 'center', background: '#f7fafe' }"
          >
            <el-table-column type="index" label="排序" width="70" align="center" />
            <el-table-column prop="engineName" label="搜索引擎" :sortable="true" align="center" />
            <el-table-column prop="pageCount" label="抓取页面数" :sortable="true" align="center" />
            <el-table-column prop="ipCount" label="IP数" :sortable="true" align="center" />
            <el-table-column prop="rateText" label="占比" :sortable="true" align="center" :sort-by="'ipCount'" />
          </el-table>
        </div>
        <div class="block-main public-hoverItem right chart-card">
          <div class="block-head">
            <div class="block-title">AI爬虫 抓取页面数 vs IP数</div>
            <el-radio-group v-model="aiDisplayMode" size="mini" class="area-mode-switch">
              <el-radio-button label="bar">条形图</el-radio-button>
              <el-radio-button label="list">列表</el-radio-button>
            </el-radio-group>
          </div>
          <div v-show="aiDisplayMode === 'bar'" ref="aiChart" class="chart-box" />
          <el-table
            v-show="aiDisplayMode === 'list'"
            :data="aiTableItems"
            size="small"
            border
            height="320"
            class="area-list-table"
            :header-cell-style="{ textAlign: 'center', background: '#f7fafe' }"
          >
            <el-table-column type="index" label="排序" width="70" align="center" />
            <el-table-column prop="sourceName" label="AI爬虫" :sortable="true" align="center" />
            <el-table-column prop="pageCount" label="抓取页面数" :sortable="true" align="center" />
            <el-table-column prop="ipCount" label="IP数" :sortable="true" align="center" />
            <el-table-column prop="rateText" label="占比" :sortable="true" align="center" :sort-by="'ipCount'" />
          </el-table>
        </div>
      </div>

      <!-- 引擎抓取页面 / AI爬虫抓取页面 -->
      <div class="block-line">
        <div class="block-main public-hoverItem left chart-card">
          <div class="block-head">
            <div class="block-title">引擎抓取页面</div>
            <div class="table-filters">
              <el-select
                v-model="engineFilter"
                placeholder="搜索引擎 全部"
                size="small"
                clearable
                class="filter-select"
                @change="enginePageNum = 1; fetchEnginePages()"
              >
                <el-option label="全部" value="" />
                <el-option v-for="item in engineOptions" :key="item.engine" :label="item.engineName" :value="item.engine" />
              </el-select>
              <el-input
                v-model="urlKeyword"
                placeholder="请输入页面地址"
                size="small"
                clearable
                class="filter-input"
                @keyup.enter.native="enginePageNum = 1; fetchEnginePages()"
              >
                <el-button slot="append" icon="el-icon-search" @click="enginePageNum = 1; fetchEnginePages()" />
              </el-input>
            </div>
          </div>
          <el-table :data="enginePageList" size="small" border style="width: 100%" :header-cell-style="{ textAlign: 'center', background: '#f7fafe' }" :default-sort="{ prop: 'crawlCount', order: 'descending' }" @sort-change="onEngineSortChange">
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="url" label="页面" min-width="200" sortable="custom">
              <template slot-scope="scope">
                <div class="page-name">{{ scope.row.title }}</div>
                <div class="page-url">{{ scope.row.url }}</div>
              </template>
            </el-table-column>
            <el-table-column prop="engines" label="搜索引擎" align="center" width="150" sortable="custom">
              <template slot-scope="scope">
                <el-tooltip :content="scope.row.engines.join('、')" placement="top" effect="dark">
                  <div class="tag-clamp">
                    <el-tag
                      v-for="name in scope.row.engines"
                      :key="name"
                      size="mini"
                      :color="tagColor(name)"
                      style="margin: 2px; border: none; color: #fff;"
                    >{{ name }}</el-tag>
                  </div>
                </el-tooltip>
              </template>
            </el-table-column>
            <el-table-column prop="crawlCount" label="总抓取次数" sortable="custom" align="center">
              <template slot-scope="scope">
                <el-tooltip placement="top" effect="dark">
                  <div slot="content">
                    <div v-for="(item, i) in scope.row.engineCounts" :key="i">{{ item.engineName }}: {{ item.crawlCount }}</div>
                  </div>
                  <span class="crawl-count-cell">{{ scope.row.crawlCount }}</span>
                </el-tooltip>
              </template>
            </el-table-column>
            <el-table-column prop="lastCrawlTime" label="最后抓取时间" sortable="custom" align="center">
              <template slot-scope="scope">{{ scope.row.lastCrawlTime || "--" }}</template>
            </el-table-column>
          </el-table>
          <el-pagination
            class="table-pagination"
            layout="total, prev, pager, next"
            :total="enginePageTotal"
            :page-size="enginePageSize"
            :current-page.sync="enginePageNum"
            @current-change="fetchEnginePages()"
          />
        </div>
        <div class="block-main public-hoverItem right chart-card">
          <div class="block-head">
            <div class="block-title">AI爬虫抓取页面</div>
            <div class="table-filters">
              <el-select
                v-model="aiSourceFilter"
                placeholder="AI爬虫 全部"
                size="small"
                clearable
                class="filter-select"
                @change="aiPageNum = 1; fetchAiPages()"
              >
                <el-option label="全部" value="" />
                <el-option v-for="item in aiSourceOptions" :key="item.source" :label="item.sourceName" :value="item.source" />
              </el-select>
              <el-input
                v-model="aiUrlKeyword"
                placeholder="请输入页面地址"
                size="small"
                clearable
                class="filter-input"
                @keyup.enter.native="aiPageNum = 1; fetchAiPages()"
              >
                <el-button slot="append" icon="el-icon-search" @click="aiPageNum = 1; fetchAiPages()" />
              </el-input>
            </div>
          </div>
          <el-table :data="aiPageList" size="small" border style="width: 100%" :header-cell-style="{ textAlign: 'center', background: '#f7fafe' }" :default-sort="{ prop: 'crawlCount', order: 'descending' }" @sort-change="onAiSortChange">
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="url" label="页面" min-width="200" sortable="custom">
              <template slot-scope="scope">
                <div class="page-name">{{ scope.row.title }}</div>
                <div class="page-url">{{ scope.row.url }}</div>
              </template>
            </el-table-column>
            <el-table-column prop="sources" label="AI爬虫" align="center" width="170" sortable="custom">
              <template slot-scope="scope">
                <el-tooltip :content="scope.row.sources.join('、')" placement="top" effect="dark">
                  <div class="tag-clamp">
                    <el-tag
                      v-for="name in scope.row.sources"
                      :key="name"
                      size="mini"
                      :color="tagColor(name)"
                      style="margin: 2px; border: none; color: #fff;"
                    >{{ name }}</el-tag>
                  </div>
                </el-tooltip>
              </template>
            </el-table-column>
            <el-table-column prop="crawlCount" label="总抓取次数" sortable="custom" align="center">
              <template slot-scope="scope">
                <el-tooltip placement="top" effect="dark">
                  <div slot="content">
                    <div v-for="(item, i) in scope.row.sourceCounts" :key="i">{{ item.sourceName }}: {{ item.crawlCount }}</div>
                  </div>
                  <span class="crawl-count-cell">{{ scope.row.crawlCount }}</span>
                </el-tooltip>
              </template>
            </el-table-column>
            <el-table-column prop="lastCrawlTime" label="最后抓取时间" sortable="custom" align="center">
              <template slot-scope="scope">{{ scope.row.lastCrawlTime || "--" }}</template>
            </el-table-column>
          </el-table>
          <el-pagination
            class="table-pagination"
            layout="total, prev, pager, next"
            :total="aiPageTotal"
            :page-size="aiPageSize"
            :current-page.sync="aiPageNum"
            @current-change="fetchAiPages()"
          />
        </div>
      </div>

      <!-- 机器人IP明细 -->
      <div class="block-line">
        <div class="block-main public-hoverItem overview-card">
          <div class="block-head">
            <div class="block-title">机器人IP明细</div>
            <div class="table-filters">
              <el-select
                v-model="categoryFilter"
                placeholder="分类 全部"
                size="small"
                clearable
                class="filter-select"
                @change="ipPageNum = 1; fetchIpDetail()"
              >
                <el-option label="全部" value="" />
                <el-option v-for="item in categoryOptions" :key="item.botCategory" :label="item.categoryName" :value="item.botCategory" />
              </el-select>
              <el-input
                v-model="ipKeyword"
                placeholder="请输入机器人请求IP"
                size="small"
                clearable
                class="filter-input"
                @keyup.enter.native="ipPageNum = 1; fetchIpDetail()"
              >
                <el-button slot="append" icon="el-icon-search" @click="ipPageNum = 1; fetchIpDetail()" />
              </el-input>
            </div>
          </div>
          <el-table :data="ipDetailList" size="small" border style="width: 100%" :header-cell-style="{ textAlign: 'center', background: '#f7fafe' }" :default-sort="{ prop: 'lastVisitTime', order: 'descending' }" @sort-change="onIpSortChange">
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="ip" label="IP" sortable="custom" align="center" />
            <el-table-column prop="region" label="地域" sortable="custom" align="center">
              <template slot-scope="scope">{{ scope.row.country }} {{ scope.row.province }}</template>
            </el-table-column>
            <el-table-column prop="botCategory" label="分类" sortable="custom" align="center">
              <template slot-scope="scope">{{ scope.row.categoryName }}</template>
            </el-table-column>
            <el-table-column prop="uaOwner" label="UA归属" sortable="custom" align="center">
              <template slot-scope="scope">{{ scope.row.uaOwnerName || scope.row.uaOwner || "--" }}</template>
            </el-table-column>
            <el-table-column prop="hitReasons" label="命中原因" align="center">
              <template slot-scope="scope">{{ scope.row.hitReasons || "--" }}</template>
            </el-table-column>
            <el-table-column prop="pv" label="浏览量" sortable="custom" align="center" />
            <el-table-column prop="visitCount" label="访问次数" sortable="custom" align="center" />
            <el-table-column prop="avgPv" label="平均访问页数" sortable="custom" align="center" />
            <el-table-column prop="avgVisitTime" label="平均访问时长" sortable="custom" align="center">
              <template slot-scope="scope">{{ formatDuration(scope.row.avgVisitTime) }}</template>
            </el-table-column>
            <el-table-column prop="lastVisitTime" label="最后访问时间" sortable="custom" align="center" width="150">
              <template slot-scope="scope">{{ scope.row.lastVisitTime || "--" }}</template>
            </el-table-column>
          </el-table>
          <el-pagination
            class="table-pagination"
            layout="total, prev, pager, next"
            :total="ipDetailTotal"
            :page-size="ipPageSize"
            :current-page.sync="ipPageNum"
            @current-change="fetchIpDetail()"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import echarts from "echarts";
import resize from "@/components/Charts/mixins/resize";
import { formatTime } from "@/utils/format";
import { averageRules } from "@/utils/percent";
import {
  getBotOverviewApi,
  getBotCategoryApi,
  getBotAreaApi,
  getBotEngineSummaryApi,
  getBotEnginePagesApi,
  getBotIpDetailApi,
  getAiCrawlerSummaryApi,
  getAiCrawlerPagesApi,
} from "@/api/trackingapi/bot";

const CATEGORY_COLORS = {
  search_engine: "#3B7BFF",
  ai_crawler: "#F5A623",
  script_crawler: "#2BC3C3",
  headless_browser: "#55C486",
  other: "#F2C94C",
};

const CATEGORY_NAMES = {
  search_engine: "搜索引擎",
  ai_crawler: "AI爬虫",
  script_crawler: "脚本爬虫",
  headless_browser: "无头浏览器",
  other: "未知/可疑机器人",
};

// IP2LOCATION 部分国家返回两位码，此处映射为中文名
const COUNTRY_CODE_NAMES = {
  gb: "英国",
  us: "美国",
  jp: "日本",
  kr: "韩国",
  ru: "俄罗斯",
  de: "德国",
  fr: "法国",
  in: "印度",
};

const AI_SOURCE_NAMES = {
  meta: "Meta",
  bytedance: "字节跳动",
  "reflection-ai": "Reflection AI",
  anthropic: "Anthropic",
  openai: "OpenAI",
  duckassist: "DuckDuckGo AI",
  perplexity: "Perplexity",
  deepseek: "DeepSeek",
  youchat: "You",
  commoncrawl: "Common Crawl",
  amazon: "Amazon",
  cohere: "Cohere",
  iask: "iAsk",
  mistral: "Mistral",
  "applebot-extended": "Applebot扩展",
};

const ENGINE_NAMES = {
  baidu: "百度",
  google: "谷歌",
  bing: "必应",
  sogou: "搜狗",
  qihoo360: "360搜索",
  yisou: "神马",
  yandex: "Yandex",
  duckduckgo: "DuckDuckGo",
  apple: "苹果",
  huawei: "华为",
  brave: "Brave",
  other: "其他",
};

// 标签区分色板：按名称稳定散列取色，同名标签颜色固定
const TAG_PALETTE = [
  "#3B7BFF", "#F5A623", "#2BC3C3", "#55C486", "#9B59B6", "#E67E22",
  "#F06292", "#4D6BFE", "#10A37F", "#DE5833", "#E0A800", "#6E6E6E",
];

export default {
  name: "BotAnalysis",
  components: {},
  mixins: [resize],
  data() {
    return {
      timeFlag: "today",
      dateRange: [],
      categoryDisplayMode: "pie",
      areaDisplayMode: "bar",
      engineDisplayMode: "bar",
      aiDisplayMode: "bar",
      // 日期快捷按钮：与趋势分析（filterBar）保持一致
      pickerOptions: {
        shortcuts: [
          {
            text: "近一周",
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
              picker.$emit("pick", [start, end]);
            },
          },
          {
            text: "近一个月",
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
              picker.$emit("pick", [start, end]);
            },
          },
          {
            text: "近三个月",
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
              picker.$emit("pick", [start, end]);
            },
          },
          {
            text: "近六个月",
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 180);
              picker.$emit("pick", [start, end]);
            },
          },
          {
            text: "近一年",
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 365);
              picker.$emit("pick", [start, end]);
            },
          },
        ],
      },
      filterBarParams: {
        startTime: "",
        endTime: "",
      },
      overviewRows: [],
      categoryList: [],
      categoryTotalIpCount: 0,
      areaList: [],
      engineSummaryList: [],
      // 引擎抓取页面
      enginePageList: [],
      enginePageTotal: 0,
      enginePageNum: 1,
      enginePageSize: 10,
      engineFilter: "",
      urlKeyword: "",
      engineSortField: "",
      engineSortOrder: "desc",
      aiSummaryList: [],
      aiPageList: [],
      aiPageTotal: 0,
      aiPageNum: 1,
      aiPageSize: 10,
      aiSourceFilter: "",
      aiUrlKeyword: "",
      aiSortField: "",
      aiSortOrder: "desc",
      // 机器人IP明细
      ipDetailList: [],
      ipDetailTotal: 0,
      ipPageNum: 1,
      ipPageSize: 10,
      categoryFilter: "",
      ipKeyword: "",
      ipSortField: "",
      ipSortOrder: "desc",
      charts: {},
    };
  },
  computed: {
    projectName() {
      return this.$store.getters.projectName;
    },
    botRow() {
      return this.overviewRows[0] || {};
    },
    humanRow() {
      return this.overviewRows[1] || {};
    },
    commonParams() {
      const { projectName } = this;
      const params = Object.assign({ projectName }, this.filterBarParams);
      // bot 聚合表按天统计，时间边界不做周/月扩展
      params.timeType = "day";
      return params;
    },
    engineOptions() {
      return this.engineSummaryList.map((item) => ({
        engine: item.engine,
        engineName: item.engineName,
      }));
    },
    categoryOptions() {
      return this.categoryList.map((item) => ({
        botCategory: item.botCategory,
        categoryName: item.categoryName,
      }));
    },
    aiSourceOptions() {
      return this.aiSummaryList.map((item) => ({
        source: item.source,
        sourceName: item.sourceName,
      }));
    },
    // 地域列表数据：国内按省份、海外按国家汇总，按 IP 数降序，占比以全部地域为分母
    areaRegionItems() {
      const inChina = this.areaList
        .filter((x) => x.region === "国内")
        .map((x) => ({ name: x.province, ipCount: x.ipCount, isChina: true }));
      const overseasMap = {};
      this.areaList
        .filter((x) => x.region === "海外")
        .forEach((x) => {
          const countryName = COUNTRY_CODE_NAMES[x.country] || x.country;
          if (!overseasMap[countryName]) {
            overseasMap[countryName] = { name: countryName, ipCount: 0, isChina: false };
          }
          overseasMap[countryName].ipCount += x.ipCount;
        });
      const items = inChina
        .concat(Object.values(overseasMap))
        .sort((a, b) => b.ipCount - a.ipCount);
      const total = items.reduce((s, x) => s + x.ipCount, 0);
      items.forEach((x) => {
        x.rateText = total > 0 ? ((x.ipCount * 100.0) / total).toFixed(2) + "%" : "0.00%";
      });
      return items;
    },
    // 分类列表数据：按 IP 数降序（接口已按 ip_count desc 返回），占比直接用接口的 ipRatio（两位小数）
    categoryTableItems() {
      return this.categoryList.map((x) => ({
        botCategory: x.botCategory,
        categoryName: x.categoryName,
        ipCount: x.ipCount,
        rateText: (x.ipRatio || 0) + "%",
      }));
    },
    // 搜索引擎汇总列表：按 IP 数降序 + 占比
    engineTableItems() {
      const items = this.engineSummaryList
        .map((x) => ({ engineName: x.engineName, pageCount: x.pageCount, ipCount: x.ipCount }))
        .sort((a, b) => b.ipCount - a.ipCount);
      const total = items.reduce((s2, x) => s2 + x.ipCount, 0);
      items.forEach((x) => {
        x.rateText = total > 0 ? ((x.ipCount * 100.0) / total).toFixed(2) + "%" : "0.00%";
      });
      return items;
    },
    // AI爬虫汇总列表：按 IP 数降序 + 占比
    aiTableItems() {
      const items = this.aiSummaryList
        .map((x) => ({ sourceName: x.sourceName, pageCount: x.pageCount, ipCount: x.ipCount }))
        .sort((a, b) => b.ipCount - a.ipCount);
      const total = items.reduce((s2, x) => s2 + x.ipCount, 0);
      items.forEach((x) => {
        x.rateText = total > 0 ? ((x.ipCount * 100.0) / total).toFixed(2) + "%" : "0.00%";
      });
      return items;
    },
  },
  watch: {
    areaDisplayMode(val) {
      // 切换显示方式时容器尺寸可能变化，触发一次 resize
      this.$nextTick(() => {
        if (val === "bar" && this.charts.barChart) {
          this.charts.barChart.resize();
        }
      });
    },
    categoryDisplayMode(val) {
      if (val === "pie" && this.charts.categoryChart) {
        this.$nextTick(() => this.charts.categoryChart.resize());
      }
    },
    engineDisplayMode(val) {
      if (val === "bar" && this.charts.engineChart) {
        this.$nextTick(() => this.charts.engineChart.resize());
      }
    },
    aiDisplayMode(val) {
      if (val === "bar" && this.charts.aiChart) {
        this.$nextTick(() => this.charts.aiChart.resize());
      }
    },
    commonParams(val) {
      this.fetchOverview(val);
      this.fetchCategory(val);
      this.fetchArea(val);
      this.fetchEngineSummary(val);
      this.fetchAiSummary(val);
      this.enginePageNum = 1;
      this.fetchEnginePages(Object.assign({}, val));
      this.aiPageNum = 1;
      this.fetchAiPages(Object.assign({}, val));
      this.ipPageNum = 1;
      this.fetchIpDetail(Object.assign({}, val));
    },
  },
  created() {
    const today = this.fmt(new Date());
    this.applyRange(today, today);
  },
  mounted() {
    this.$nextTick(() => {
      this.initCharts();
      // 筛选栏（子组件）先于本组件挂载即触发查询，数据可能先于图表就绪，补渲染一次
      this.renderCategoryChart();
      this.renderAreaChart();
      this.renderEngineChart();
      this.renderAiSummaryChart();
    });
  },
  beforeDestroy() {
    Object.keys(this.charts).forEach((key) => {
      if (this.charts[key]) {
        this.charts[key].dispose();
        this.charts[key] = null;
      }
    });
  },
  methods: {
    fmt(d) {
      const p2 = (n) => (n < 10 ? "0" + n : "" + n);
      return d.getFullYear() + "-" + p2(d.getMonth() + 1) + "-" + p2(d.getDate());
    },
    applyRange(start, end) {
      this.filterBarParams.startTime = start;
      this.filterBarParams.endTime = end;
      this.dateRange = [start, end];
    },
    handleTimeChange(val) {
      const today = this.fmt(new Date());
      let start = today;
      if (val === "week") {
        const d = new Date();
        start = this.fmt(new Date(d.getTime() - ((d.getDay() + 6) % 7) * 86400000));
      } else if (val === "month") {
        const d = new Date();
        start = this.fmt(new Date(d.getFullYear(), d.getMonth(), 1));
      }
      this.applyRange(start, today);
    },
    handleDateChange(val) {
      if (val && val.length === 2) {
        this.applyRange(val[0], val[1]);
      }
    },
    formatDuration(seconds) {
      if (!seconds || seconds <= 0) {
        return "--";
      }
      const total = Math.round(seconds);
      const m = Math.floor(total / 60);
      const s = total % 60;
      return (m < 10 ? "0" + m : m) + ":" + (s < 10 ? "0" + s : s);
    },
    averageRulesEvent(num) {
      return averageRules(num);
    },
    formatTimeFun(val) {
      if (!val || val <= 0) {
        return "";
      }
      return formatTime(Math.floor(val));
    },
    // 空数据占位：与 pieMarker 空状态一致，居中提示"暂无数据"
    setChartEmpty(chart) {
      chart.setOption(
        {
          title: {
            text: "暂无数据",
            left: "center",
            top: "center",
            textStyle: { color: "#999", fontSize: 14, fontWeight: "normal" },
          },
          series: [],
          legend: {},
          tooltip: { show: false },
        },
        { notMerge: true }
      );
    },
    initCharts() {
      const refs = ["categoryChart", "barChart", "engineChart", "aiChart"];
      refs.forEach((ref) => {
        if (this.$refs[ref]) {
          this.charts[ref] = echarts.init(this.$refs[ref]);
        }
      });
    },
    fetchOverview(params) {
      getBotOverviewApi(params).then((res) => {
        if (res.code == 200 && res.data) {
          const bot = res.data.bot || {};
          const human = res.data.human || {};
          const row = (name, data) => ({
            name,
            pv: data.pv || 0,
            visitCount: data.visitCount || 0,
            uv: data.uv || 0,
            ipCount: data.ipCount || 0,
            avgPv: data.avgPv || 0,
            avgVisitTime: data.avgVisitTime || 0,
          });
          this.overviewRows = [row("机器人", bot), row("真人", human)];
        }
      });
    },
    fetchCategory(params) {
      getBotCategoryApi(params).then((res) => {
        if (res.code == 200 && res.data) {
          this.categoryList = res.data.list || [];
          this.categoryTotalIpCount = res.data.totalIpCount || 0;
          this.renderCategoryChart();
        }
      });
    },
    renderCategoryChart() {
      const chart = this.charts.categoryChart;
      if (!chart) {
        return;
      }
      if (!this.categoryList.length || !this.categoryTotalIpCount) {
        this.setChartEmpty(chart);
        return;
      }
      const list = this.categoryList;
      const option = {
        tooltip: {
          trigger: "item",
          confine: true,
          formatter: (p) =>
            p.name + "<br/>请求IP数: " + p.value + "<br/>占比: " + p.percent.toFixed(2) + "%",
        },
        legend: { bottom: 0, icon: "circle", itemWidth: 10, itemHeight: 10 },
        title: {
          text: String(this.categoryTotalIpCount),
          subtext: "机器人请求IP数",
          left: "center",
          top: "42%",
          textStyle: { fontSize: 22, color: "#303133" },
          subtextStyle: { fontSize: 12, color: "#909399" },
        },
        series: [
          {
            name: "机器人分类",
            type: "pie",
            radius: ["48%", "70%"],
            center: ["50%", "46%"],
            avoidLabelOverlap: true,
            label: { formatter: "{b} {d}%", fontSize: 12 },
            data: list.map((item) => ({
              name: item.categoryName,
              value: item.ipCount,
              itemStyle: { color: CATEGORY_COLORS[item.botCategory] || "#909399" },
            })),
          },
        ],
      };
      chart.setOption(option, true);
    },
    fetchArea(params) {
      getBotAreaApi(params).then((res) => {
        if (res.code == 200 && res.data) {
          const list = res.data.list || [];
          const inChina = list.filter((x) => x.region === "国内");
          const overseas = list.filter((x) => x.region === "海外");
          inChina.sort((a, b) => b.ipCount - a.ipCount);
          overseas.sort((a, b) => b.ipCount - a.ipCount);
          this.areaList = inChina.concat(overseas);
          this.renderAreaBarChart();
        }
      });
    },
    renderAreaBarChart() {
      const chart = this.charts.barChart;
      if (!chart) {
        return;
      }
      if (!this.areaList.length) {
        this.setChartEmpty(chart);
        return;
      }
      // 国内按省份展示，海外按国家汇总展示
      const inChina = this.areaList
        .filter((x) => x.region === "国内")
        .sort((a, b) => b.ipCount - a.ipCount)
        .map((x) => ({ name: x.province, ipCount: x.ipCount, pv: x.pv, visitCount: x.visitCount }));
      const overseasMap = {};
      this.areaList
        .filter((x) => x.region === "海外")
        .forEach((x) => {
          const countryName = COUNTRY_CODE_NAMES[x.country] || x.country;
          if (!overseasMap[countryName]) {
            overseasMap[countryName] = { name: countryName, ipCount: 0, pv: 0, visitCount: 0 };
          }
          overseasMap[countryName].ipCount += x.ipCount;
          overseasMap[countryName].pv += x.pv;
          overseasMap[countryName].visitCount += x.visitCount;
        });
      const overseasItems = Object.values(overseasMap).sort((a, b) => b.ipCount - a.ipCount);
      // 类目过多时标签互相重叠，各取 IP 数 Top10 展示（精确数值见机器人IP明细）
      const chartItems = inChina.slice(0, 10).concat(overseasItems.slice(0, 10));
      const names = chartItems.map((x) => x.name);
      const option = {
        tooltip: { trigger: "axis", confine: true },
        legend: { bottom: 0, icon: "circle", itemWidth: 10, itemHeight: 10 },
        grid: { left: 50, right: 16, top: 20, bottom: 56 },
        xAxis: { type: "category", data: names, axisLabel: { interval: 0, rotate: 40, fontSize: 11 } },
        yAxis: { type: "value" },
        series: [
          {
            name: "机器人IP数",
            type: "bar",
            barMaxWidth: 18,
            itemStyle: { color: "#F5A623" },
            data: chartItems.map((x) => x.ipCount),
          },
          {
            name: "访问次数",
            type: "bar",
            barMaxWidth: 18,
            itemStyle: { color: "#3B7BFF" },
            data: chartItems.map((x) => x.visitCount),
          },
        ],
      };
      chart.setOption(option, true);
    },
    fetchEngineSummary(params) {
      getBotEngineSummaryApi(params).then((res) => {
        if (res.code == 200 && res.data) {
          this.engineSummaryList = res.data.list || [];
          this.renderEngineChart();
        }
      });
    },
    renderEngineChart() {
      const chart = this.charts.engineChart;
      if (!chart) {
        return;
      }
      if (!this.engineSummaryList.length) {
        this.setChartEmpty(chart);
        return;
      }
      const list = this.engineSummaryList;
      const names = list.map((x) => x.engineName);
      const option = {
        tooltip: {
          trigger: "axis",
          confine: true,
          formatter: (params) => {
            const item = this.engineSummaryList[params[0].dataIndex] || {};
            let html = item.engineName;
            if (item.pattern) {
              html += "<br/>判断规则: " + item.pattern;
            }
            params.forEach((p) => {
              html += "<br/>" + p.marker + p.seriesName + ": " + p.value;
            });
            return html;
          },
        },
        legend: { bottom: 0, icon: "circle", itemWidth: 10, itemHeight: 10 },
        grid: { left: 10, right: 30, top: 20, bottom: 40, containLabel: true },
        yAxis: {
          type: "category",
          data: names,
          inverse: true,
          axisLabel: { formatter: (v) => (v.length > 12 ? v.slice(0, 12) + "…" : v) },
        },
        xAxis: { type: "value" },
        series: [
          {
            name: "抓取页面数",
            type: "bar",
            barMaxWidth: 14,
            itemStyle: { color: "#3B7BFF" },
            data: list.map((x) => x.pageCount),
          },
          {
            name: "IP数",
            type: "bar",
            barMaxWidth: 14,
            itemStyle: { color: "#8FB6FF" },
            data: list.map((x) => x.ipCount),
          },
        ],
      };
      chart.setOption(option, true);
    },
    aiSourceName(key) {
      return AI_SOURCE_NAMES[key] || key;
    },
    tagColor(name) {
      let hash = 0;
      for (let i = 0; i < name.length; i++) {
        hash = (hash * 31 + name.charCodeAt(i)) >>> 0;
      }
      return TAG_PALETTE[hash % TAG_PALETTE.length];
    },
    fetchAiSummary(params) {
      getAiCrawlerSummaryApi(params).then((res) => {
        if (res.code == 200 && res.data) {
          this.aiSummaryList = res.data.list || [];
          this.renderAiSummaryChart();
        }
      });
    },
    renderAiSummaryChart() {
      const chart = this.charts.aiChart;
      if (!chart) {
        return;
      }
      if (!this.aiSummaryList.length) {
        this.setChartEmpty(chart);
        return;
      }
      const list = this.aiSummaryList;
      const option = {
        tooltip: {
          trigger: "axis",
          confine: true,
          formatter: (params) => {
            const item = this.aiSummaryList[params[0].dataIndex] || {};
            let html = item.sourceName;
            if (item.pattern) {
              html += "<br/>判断规则: " + item.pattern;
            }
            params.forEach((p) => {
              html += "<br/>" + p.marker + p.seriesName + ": " + p.value;
            });
            return html;
          },
        },
        legend: { bottom: 0, icon: "circle", itemWidth: 10, itemHeight: 10 },
        grid: { left: 10, right: 30, top: 20, bottom: 40, containLabel: true },
        yAxis: {
          type: "category",
          data: list.map((x) => x.sourceName),
          inverse: true,
          axisLabel: { formatter: (v) => (v.length > 12 ? v.slice(0, 12) + "…" : v) },
        },
        xAxis: { type: "value" },
        series: [
          {
            name: "抓取页面数",
            type: "bar",
            barMaxWidth: 14,
            itemStyle: { color: "#F5A623" },
            data: list.map((x) => x.pageCount),
          },
          {
            name: "IP数",
            type: "bar",
            barMaxWidth: 14,
            itemStyle: { color: "#8FB6FF" },
            data: list.map((x) => x.ipCount),
          },
        ],
      };
      chart.setOption(option, true);
    },
    fetchAiPages(paramsOverride) {
      const params = Object.assign({}, paramsOverride || this.commonParams, {
        pageNum: this.aiPageNum,
        pageSize: this.aiPageSize,
        source: this.aiSourceFilter || "",
        urlKeyword: this.aiUrlKeyword || "",
        sortField: this.aiSortField || "",
        sortOrder: this.aiSortOrder || "desc",
      });
      getAiCrawlerPagesApi(params).then((res) => {
        if (res.code == 200 && res.data) {
          this.aiPageList = res.data.list || [];
          this.aiPageTotal = res.data.total || 0;
        }
      });
    },
    fetchEnginePages(paramsOverride) {
      const params = Object.assign({}, paramsOverride || this.commonParams, {
        pageNum: this.enginePageNum,
        pageSize: this.enginePageSize,
        engine: this.engineFilter || "",
        urlKeyword: this.urlKeyword || "",
        sortField: this.engineSortField || "",
        sortOrder: this.engineSortOrder || "desc",
      });
      getBotEnginePagesApi(params).then((res) => {
        if (res.code == 200 && res.data) {
          this.enginePageList = res.data.list || [];
          this.enginePageTotal = res.data.total || 0;
        }
      });
    },
    fetchIpDetail(paramsOverride) {
      const params = Object.assign({}, paramsOverride || this.commonParams, {
        pageNum: this.ipPageNum,
        pageSize: this.ipPageSize,
        botCategory: this.categoryFilter || "",
        ipKeyword: this.ipKeyword || "",
        sortField: this.ipSortField || "",
        sortOrder: this.ipSortOrder || "desc",
      });
      getBotIpDetailApi(params).then((res) => {
        if (res.code == 200 && res.data) {
          this.ipDetailList = res.data.list || [];
          this.ipDetailTotal = res.data.total || 0;
        }
      });
    },
    onEngineSortChange({ prop, order }) {
      this.engineSortField = order ? prop : "";
      this.engineSortOrder = order === "ascending" ? "asc" : "desc";
      this.enginePageNum = 1;
      this.fetchEnginePages();
    },
    onAiSortChange({ prop, order }) {
      this.aiSortField = order ? prop : "";
      this.aiSortOrder = order === "ascending" ? "asc" : "desc";
      this.aiPageNum = 1;
      this.fetchAiPages();
    },
    onIpSortChange({ prop, order }) {
      this.ipSortField = order ? prop : "";
      this.ipSortOrder = order === "ascending" ? "asc" : "desc";
      this.ipPageNum = 1;
      this.fetchIpDetail();
    },
  },
};
</script>

<style lang="scss" scoped>
@import "~@/styles/components/custom-select.scss";
.bot-analysis-page {
  // 顶部筛选栏：对齐数据概览 TopFilter 的白底/内边距/下边框样式
  .bot-filter-bar {
    position: sticky;
    top: 0;
    z-index: 100;
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    padding: 15px 20px;
    background: #fff;
    border-bottom: 1px #eee solid;
    // 时间组边框左缘与下方卡片左缘对齐
    .radio-com {
      margin-left: 0;
    }
    .filter-date {
      margin-left: 16px;
      width: 280px;
    }
    .el-radio {
      margin-right: 12px;
    }
  }
  .block-line {
    box-sizing: border-box;
    margin: 20px 0;
    display: flex;
    justify-content: space-between;
    .left {
      width: 50%;
      margin-right: 10px;
    }
    .right {
      width: 50%;
      margin-left: 10px;
    }
  }
    .overview-card {
      // 通栏卡片（流量概览/机器人IP明细）：block-line 是 flex 容器，需显式占满宽度
      width: 100%;
      .block-head {
        margin-bottom: 0;
      }
      // 机器人IP明细：搜索框与表格之间留出间距（表格仅此卡片存在）
      .el-table {
        margin-top: 10px;
        // 表格四角圆角，overflow 裁掉表头背景的直角
        border-radius: 6px;
        overflow: hidden;
      }
    // 流量概览无边框行布局，样式对齐数据概览 FlowView
    .bid-list-page {
      display: flex;
      flex-direction: column;
      .bid-list-header {
        display: flex;
        margin-bottom: 12px;
        .header-name {
          width: calc(100% / 7);
          height: 30px;
          display: flex;
          align-items: center;
          justify-content: center;
          position: relative;
          .icon-question {
            width: 11.44px;
            height: 11.44px;
            margin-left: 6px;
            cursor: pointer;
            position: absolute;
            transform: translate(0, -50%);
          }
        }
        .w156 {
          font-size: 13px;
          font-weight: 400;
          line-height: 14px;
          color: #4d4d4d;
        }
      }
      .bid-list-record {
        display: flex;
        margin-bottom: 11px;
        .bid-list-item1 {
          width: calc(100% / 7);
          height: 30px;
          display: flex;
          align-items: center;
          justify-content: flex-start;
        }
        .bid-list-item {
          width: calc(100% / 7);
          height: 30px;
          display: flex;
          align-items: center;
          justify-content: center;
        }
        .w157 {
          font-size: 13px;
          font-weight: 400;
          line-height: 14px;
          color: #4d4d4d;
        }
        // 机器人/真人两行统计数字统一：15px 常规字重 #4d4d4d
        .w158 {
          font-size: 15px;
          font-weight: 400;
          color: #4d4d4d;
          line-height: 16px;
        }
        .w159 {
          font-size: 15px;
          font-weight: 400;
          line-height: 16px;
          color: #4d4d4d;
        }
      }
    }
  }
  .chart-card {
    .chart-box {
      height: 320px;
      width: 100%;
    }
    // 引擎抓取页面/AI爬虫抓取页面：表格四角圆角，与机器人IP明细一致
    .el-table {
      border-radius: 6px;
      overflow: hidden;
    }
  }
  .block-head {
    .table-filters {
      display: flex;
      align-items: center;
      margin-left: auto;
      .filter-select {
        width: 160px;
        margin-right: 10px;
      }
      .filter-input {
        width: 240px;
      }
    }
    .area-mode-switch {
      margin-left: auto;
    }
  }
  // 4 个新增列表：与机器人IP明细一致的圆角
  .area-list-table {
    border-radius: 6px;
    overflow: hidden;
  }
  .tag-clamp {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    align-items: flex-start;
    max-height: 48px;
    overflow: hidden;
    line-height: 22px;
    text-align: center;
  }
  .page-name {
    color: #303133;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .page-url {
    color: #909399;
    font-size: 12px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .table-pagination {
    text-align: right;
    padding: 10px 0;
  }
}
</style>
