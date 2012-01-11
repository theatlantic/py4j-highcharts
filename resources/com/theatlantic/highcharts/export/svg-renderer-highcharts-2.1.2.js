var jsonReviver;
(function() {
    var unescapeHtmlDiv;
    var unescapeHtml = function(str) {
        if (!unescapeHtmlDiv) {
            unescapeHtmlDiv = Highcharts.createElement('div', null, null, null, true);        
        }
		div.innerHTML = str;
		return div.innerText || div.text || div.textContent;
	};
	
	var dateRegex = new RegExp(/^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2})/);
	jsonReviver = function(key, value) {
    	var m;
    	if (typeof value == 'string') {
    		m = dateRegex.exec(value);
    		if (m) {
    			if (/(\.\d+)?Z?$/.exec(value)) {
    				return (new Date(Date.UTC(+m[1], +m[2] - 1, +m[3], +m[4], +m[5], +m[6]))).valueOf();
    			}
    		}
    		if (value.indexOf('&lt;') != -1) {
    			value = unescapeHtml(value);
    		} else if (value.indexOf('&gt;') != -1) {
    			value = unescapeHtml(value);
    		 } else if (value.indexOf('&quot;') != -1) {
    			value = unescapeHtml(value);
    		}

    		if (key == 'formatter') {
    			try {
    				return new Function(value);
    			} catch(e) {
    				return null;
    			}
    		}
    		// For ease of merging, we store the 'series' option as an object (with integer keys)
    		// Here we test if this is the case and then convert the value back into an array
    		if (key == 'series' && typeof(value) == 'object' && typeof(value.length) == 'undefined') {
    			var series = new Array();
    			for (var i in value) {
    				if (typeof(i) == 'number') {
    					series[i] = value[i];
    				} else if (typeof(i) == 'string') {
    					var intI = parseInt(i, 10);
    					if (i == intI.toString()) {
    						series[intI] = value[i];
    					}
    				}
    			}
    			return series;
    		}
    	}
    	return value;
    };
})();

function renderSVGFromJson(jsonChartOptions) {
    var chartOptions = JSON.parse(jsonChartOptions, jsonReviver);
	var n = Highcharts.createElement('div', null, null, null, true);
	document.body.appendChild(n);
	if (typeof chartOptions == 'object' && typeof chartOptions.chart == 'object') {
        if (typeof chartOptions.chart.height == 'number') {
            n.style.height = (chartOptions.chart.height + 10).toString + 'px';
        }
        if (typeof chartOptions.chart.width == 'number') {
            n.style.width = (chartOptions.chart.width + 10).toString + 'px';
        }
	}
	chartOptions.chart.renderTo = n;
	chartOptions.chart.renderer = 'SVG';
	
	var chart = new Highcharts.Chart(chartOptions);
	var svg = chart.getSVG();
	
    chart.recursiveDelete(chartOptions);
	chart.destroy();
	chartOptions = null;
	generalOptions = null;
    document.body.removeChild(n);
    delete n.attributes;
    delete n.childNodes;
    n = null;
	return svg;
}