package com.example.academiaui.feature_manager.util

import android.util.Log

object SubjectMapper {
    private val subjectMap = mapOf(
        "astro-ph" to "天体物理",
        "astro-ph.CO" to "宇宙学与非规则天体物理学",
        "astro-ph.EP" to "地球与行星天体物理学",
        "astro-ph.GA" to "星系的天体物理学",
        "astro-ph.HE" to "高能天体物理现象",
        "astro-ph.IM" to "天体物理学的仪器和方法",
        "astro-ph.SR" to "太阳与恒星天体物理学",
        "cond-mat" to "凝聚态物理",
        "cond-mat.dis-nn" to "无序系统与神经网络",
        "cond-mat.mes-hall" to "中尺度和纳米尺度物理学",
        "cond-mat.mtrl-sci" to "材料科学",
        "cond-mat.other" to "其他凝聚态",
        "cond-mat.quant-gas" to "量子气体",
        "cond-mat.soft" to "软凝聚物",
        "cond-mat.stat-mech" to "统计力学",
        "cond-mat.str-el" to "强关联电子",
        "cond-mat.supr-con" to "超导现象",
        "cs" to "计算机科学",
        "cs.AI" to "人工智能",
        "cs.AR" to "硬件架构",
        "cs.CC" to "计算复杂性",
        "cs.CE" to "计算工程，金融和科学",
        "cs.CG" to "计算几何",
        "cs.CL" to "计算与语言",
        "cs.CR" to "密码学与保安",
        "cs.CV" to "计算机视觉与模式识别",
        "cs.CY" to "电脑与社会",
        "cs.DB" to "数据库",
        "cs.DC" to "分布式、并行和集群计算",
        "cs.DL" to "数字仓库",
        "cs.DM" to "离散数学",
        "cs.DS" to "数据结构和算法",
        "cs.ET" to "新兴科技",
        "cs.FL" to "形式语言与自动机理论",
        "cs.GL" to "一般文学",
        "cs.GR" to "图形",
        "cs.GT" to "计算机科学与博弈论",
        "cs.HC" to "人机交互",
        "cs.IR" to "信息检索",
        "cs.IT" to "信息理论",
        "cs.LG" to "学习",
        "cs.LO" to "计算机科学中的逻辑",
        "cs.MA" to "多代理系统",
        "cs.MM" to "多媒体",
        "cs.MS" to "数学软件",
        "cs.NA" to "数值分析",
        "cs.NE" to "神经和进化计算",
        "cs.NI" to "网络与互联网架构",
        "cs.OH" to "其他计算机科学",
        "cs.OS" to "操作系统",
        "cs.PF" to "性能",
        "cs.PL" to "编程语言",
        "cs.RO" to "机器人技术",
        "cs.SC" to "符号计算",
        "cs.SD" to "声音",
        "cs.SE" to "软件工程",
        "cs.SI" to "社会和信息网络",
        "cs.SY" to "系统及控制",
        "econ" to "经济学",
        "econ.EM" to "计量经济学",
        "eess.AS" to "音频及语音处理",
        "eess.IV" to "图像和视频处理",
        "eess.SP" to "信号处理",
        "gr-qc" to "广义相对论和量子宇宙学",
        "hep-ex" to "高能物理实验",
        "hep-lat" to "高能物理-晶格",
        "hep-ph" to "高能物理-现象学",
        "hep-th" to "高能物理理论",
        "math.AC" to "交换代数",
        "math.AG" to "代数几何",
        "math.AP" to "偏微分方程分析",
        "math.AT" to "代数拓扑",
        "math.CA" to "传统分析和微分方程",
        "math.CO" to "组合数学",
        "math.CT" to "范畴理论",
        "math.CV" to "复杂变量",
        "math.DG" to "微分几何",
        "math.DS" to "动力系统",
        "math.FA" to "功能分析",
        "math.GM" to "普通数学",
        "math.GN" to "点集拓扑学",
        "math.GR" to "群论",
        "math.GT" to "几何拓扑学",
        "math.HO" to "历史和概述",
        "math.IT" to "信息理论",
        "math.KT" to "K 理论与同调",
        "math.LO" to "逻辑",
        "math.MG" to "度量几何学",
        "math.MP" to "数学物理",
        "math.NA" to "数值分析",
        "math.NT" to "数论",
        "math.OA" to "算子代数",
        "math.OC" to "优化和控制",
        "math.PR" to "概率",
        "math.QA" to "量子代数",
        "math.RA" to "环与代数",
        "math.RT" to "表示论",
        "math.SG" to "辛几何",
        "math.SP" to "光谱理论",
        "math.ST" to "统计学理论",
        "math-ph" to "数学物理",
        "nlin.AO" to "适应与自组织系统",
        "nlin.CD" to "混沌动力学",
        "nlin.CG" to "元胞自动机与格子气体",
        "nlin.PS" to "模式形成与孤子",
        "nlin.SI" to "严格可解可积系统",
        "nucl-ex" to "核试验",
        "nucl-th" to "核理论",
        "physics.acc-ph" to "加速器物理学",
        "physics.ao-ph" to "大气和海洋物理学",
        "physics.app-ph" to "应用物理学",
        "physics.atm-clus" to "原子和分子团簇",
        "physics.atom-ph" to "原子物理学",
        "physics.bio-ph" to "生物物理学",
        "physics.chem-ph" to "化学物理",
        "physics.class-ph" to "经典物理学",
        "physics.comp-ph" to "计算物理学",
        "physics.data-an" to "数据分析、统计和概率",
        "physics.ed-ph" to "物理教育",
        "physics.flu-dyn" to "流体动力学",
        "physics.gen-ph" to "普通物理",
        "physics.geo-ph" to "地球物理学",
        "physics.hist-ph" to "物理学的历史与哲学",
        "physics.ins-det" to "仪器和探测器",
        "physics.med-ph" to "医学物理学",
        "physics.optics" to "光学",
        "physics.plasm-ph" to "等离子体物理",
        "physics.pop-ph" to "大众物理",
        "physics.soc-ph" to "物理学与社会",
        "physics.space-ph" to "空间物理学",
        "q-bio.BM" to "生物分子",
        "q-bio.CB" to "细胞行为",
        "q-bio.GN" to "基因组学",
        "q-bio.MN" to "分子网络",
        "q-bio.NC" to "神经元与认知",
        "q-bio.OT" to "其他定量生物学",
        "q-bio.PE" to "种群与进化",
        "q-bio.QM" to "定量方法",
        "q-bio.SC" to "亚细胞突起",
        "q-bio.TO" to "组织和器官",
        "q-fin.CP" to "金融工程",
        "q-fin.EC" to "经济学",
        "q-fin.GN" to "财务概述",
        "q-fin.MF" to "数学金融",
        "q-fin.PM" to "投资组合管理",
        "q-fin.PR" to "证券定价",
        "q-fin.RM" to "风险管理",
        "q-fin.ST" to "金融统计",
        "q-fin.TR" to "交易与市场微观结构",
        "quant-ph" to "量子物理学",
        "stat.AP" to "应用",
        "stat.CO" to "计算",
        "stat.ME" to "方法论",
        "stat.ML" to "机器学习",
        "stat.OT" to "其他统计学",
        "stat.TH" to "统计学理论"
    )

    // 定义新的大类分类映射
    private val broaderCategoryMap = mapOf(
        "astro-ph" to "物理",
        "cond-mat" to "物理",
        "gr-qc" to "物理",
        "hep-ex" to "物理",
        "hep-lat" to "物理",
        "hep-ph" to "物理",
        "hep-th" to "物理",
        "math-ph" to "物理", // 注意：math-ph 既是 math 的子类，又是 physics 的子类，这里根据您的要求归到物理
        "nlin" to "物理",
        "nucl-ex" to "物理",
        "nucl-th" to "物理",
        "physics" to "物理",
        "quant-ph" to "物理",

        "math" to "数学",

        "cs" to "计算机科学",

        "q-bio" to "量化生物",

        "q-fin" to "量化金融",

        "stat" to "统计",

        "eess" to "电子工程与系统",

        "econ" to "经济学"
    )

    // 将字段映射到中文名称
    fun toChineseName(field: String): String {
        return subjectMap[field] ?: "未知学科"
    }

    // 获取所有学科分类，现在按照新的大类分组
    fun getAllCategories(): Map<String, List<Pair<String, String>>> {
        val categorizedFields = mutableMapOf<String, MutableList<Pair<String, String>>>()

        subjectMap.forEach { (englishField, chineseName) ->
            val originalCategoryPrefix = englishField.split('.').firstOrNull() ?: englishField // 提取原始的大类前缀，例如 "astro-ph"

            // 根据新的 broaderCategoryMap 找到它所属的中文大类
            val broaderChineseCategory = broaderCategoryMap[originalCategoryPrefix] ?: "其他" // 如果没有找到匹配的，归到“其他”

            if (!categorizedFields.containsKey(broaderChineseCategory)) {
                categorizedFields[broaderChineseCategory] = mutableListOf()
            }
            categorizedFields[broaderChineseCategory]?.add(englishField to chineseName)
        }

        // 对每个大类下的子领域按中文名称排序
        categorizedFields.forEach { (_, fields) ->
            fields.sortBy { it.second } // 根据 Pair 的 second 元素（中文名称）排序
        }

        // 为了保持大类的显示顺序一致性，可以对大类的键进行排序
        // 按照 Physics, Math, CS, etc. 的顺序
        val sortedKeys = listOf(
            "物理",
            "数学",
            "计算机科学",
            "量化生物",
            "量化金融",
            "统计",
            "电子工程与系统",
            "经济学",
            "其他" // 如果有的话
        ).filter { categorizedFields.containsKey(it) } // 确保只包含实际存在的键

        val sortedMap = linkedMapOf<String, List<Pair<String, String>>>()
        sortedKeys.forEach { key ->
            categorizedFields[key]?.let { value ->
                sortedMap[key] = value
            }
        }
        return sortedMap
    }
}