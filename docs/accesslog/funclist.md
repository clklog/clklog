 <table class="func-list-table">
    <tr>
        <th class="func-title-td">功能模块</th>
        <th>子模块</th>
        <th class="func-intro-td">功能简介</th>
        <th class="func-desc-td">核心功能</th>
    </tr>
    <tr>
        <td rowspan="1">数据概览</td>
        <td></td>
        <td>按时间段、主机（host）,对各个服务器采集的WEB服务器系统日志，从各种不同维度进行数据统计和图表展示。</td>
        <td>
        <br />
            Ø 日志访问量基础指标项（访问量PV、用户数UV、IP数、平均相应时间、流出流量）进行统计展示 <br /><br />
            Ø 根据所选时间范围，选择按时、按日、按周、按月对用户数(UV)、访问量（PV）、流出流量的变化趋势进行展示 <br /><br />
            Ø 地图模式和列表模式显示客户端IP的访问位置及位置占比信息<br /><br />
            Ø 根据响应时长显示响应时间最长的10个url信息 <br /><br />
            Ø 根据访问量显示访问量最大的10个url信息及url对应的平均耗时时长 <br /><br />
            Ø 根据客户端IP显示访问量最高的10条IP信息 <br /><br />
            Ø 根据请求UA, 识别用户浏览器，横向柱状图显示用户最大的10个浏览器，列表显示所有的浏览器及浏览器对应的用户数 <br /><br />
            Ø 根据http请求返回的状态码，统计各类型的状态码返回数量 <br /><br />
            Ø 根据访问来源页面的访问量，现在访问量对大的10个 <br /><br />
            Ø 根据请求类型，通过饼状图展示各类型请求数量及占比 <br /><br />
        </td>
    </tr>
    <tr>
        <td>趋势分析</td>
        <td></td>
        <td>从时间段、主机（host），按时、按日、按周、按月查询展示web服务器请求的各项指标数据变化趋势及数据详情</td>
        <td>
            Ø WEB服务器请求的各项指标（访问用户数、访问量、流出流量）数据的变化趋势 <br /><br />
            Ø WEB服务器请求的各项指标（访问用户数、访问量、流出流量）的数据详情 <br /><br />
        </td>
    </tr>
    <tr>
        <td rowspan="2">性能分析</td>
        <td>耗时分析</td>
        <td>对采集的受访页面URL的访问次数、耗时较长（>=1s）的次数、最大耗时、访问量展览、平均耗时指标进行统查询统计。</td>
        <td>
        <br />
            Ø WEB请求的访问次数、耗时较长的次数(>=1秒)、最大耗时、耗时较长次数、平均耗时数据的查看与统计 <br /><br />
            Ø WEB请求数据类型、耗时超过1秒的请求数据过滤 <br /><br />
        </td>
    </tr>
    <tr>
        <td >超100毫秒时序图</td>
        <td>耗时超过{100毫秒/1分钟}的Url序图</td>
        <td>指定小时内请求耗时超过{100毫秒/1分钟}的Url序图，用于对API的运行质量进行分析，监控运行耗时较长的URL，分析运行时间较长的原因。<br /></td>
    </tr>
    <tr>
        <td>状态码分析</td>
        <td></td>
        <td>对各服务器http请求返回的状态数据分析，对于有问题的状态码数据进行统计查询。</td>
        <td>
            <br /> Ø 通过饼状图、柱状图展示http请求的状态码及占比信息 <br /><br />
            Ø 二维表格展示应用相关的host以及各个host相关的状态码数据总量 <br /> <br />
            Ø 各个状态码对应的url请求数据访问次数、耗时较长次数、最大耗时、平均耗时等信息查看分析 <br /> <br />
        </td>
    </tr>
    <tr>
        <td>IP分析</td>
        <td></td>
        <td>按时间段、主机（host），按省份及国家对WEB服务器访问流量指标数据进行统计分析</td>
        <td><br />
            Ø 按省份（仅限中国）统计分析各省份的访问用户各项指标（访客数、浏览量、IP数）数据 <br /><br />
            Ø 按国家统计分析各个国家的访问用户的各项指标（访客数、浏览量、IP数）数据 <br /><br />
            Ø 按访问IP统计分析单个IP的访问详细数据（浏览量，访问页面、请求次数、请求耗时等） <br /><br />
        </td>
    </tr>
</table>
