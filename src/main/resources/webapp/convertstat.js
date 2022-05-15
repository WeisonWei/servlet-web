/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
(function () {
  "use strict";

  dust.loadSource(dust.compile($('#tmpl-convert-stat').html(), 'convert-stat'));
  dust.loadSource(dust.compile($('#tmpl-convert-stat-history').html(), 'convert-stat-history'));

  $.fn.dataTable.ext.order['ng-value'] = function (settings, col) {
    return this.api().column(col, {order:'index'} ).nodes().map(function (td, i) {
      return $(td).attr('ng-value');
    });
  };

  function show_err_msg(msg) {
    $('#alert-panel-body').html(msg);
    $('#alert-panel').show();
  }

  function load_convert_stat() {
      var BEANS = [
        {"name": "convertorstat", "url": "/jmx?qry=Hadoop:service=NameNode,name=ConvertorState"}
      ];
      var HELPERS = {
        'helper_date_tostring': function (chunk, ctx, bodies, params) {
          var value = dust.helpers.tap(params.value, chunk, ctx);
          var v = value == 0 ? '-' : '' + moment(Number(value)).format('ddd MMM DD HH:mm:ss ZZ YYYY');
          return chunk.write(v);
        },
        'helper_duration_tostring': function (chunk, ctx, bodies, params) {
          var value = dust.helpers.tap(params.value, chunk, ctx);
          var v = value == -1 ? '-' : '' + moment(Number(value)).utcOffset(0).format('HH:mm:ss');
          return chunk.write(v);
        }
      };
      var data = {};
      load_json(
        BEANS,
        function(d) {
          for (var k in d) {
            if (k === 'convertorstat') {
              if (d[k].beans.length > 0) {
                var convertingJobs = JSON.parse(d[k].beans[0].ConvertingJobs);
                data['ConvertingJobs'] = convertingJobs;
                var historyConvertJobs = JSON.parse(d[k].beans[0].HistoryConvertJobs);
                data['HistoryConvertJobs'] = historyConvertJobs;
                data['State'] = d[k].beans[0].State;
              }
            }
          }

          if (JSON.stringify(data) === '{}') {
            show_err_msg('Convertor is disabled. If you want to enable it,'
                + ' should config the dfs.class.factory.impl to'
                + ' org.apache.hadoop.hdfs.JDHdfsClassFactory.');
          } else {
            if (data['State'] === 'active') {
              render();
            } else {
              show_err_msg('Convertor is enabled only in active state.');
            }
          }
        },
        function (url, jqxhr, text, err) {
          show_err_msg('Failed to retrieve data from ' + url + ', cause: ' + err);
        });

      function render() {
        var base = dust.makeBase(HELPERS);
        dust.render('convert-stat', base.push(data), function(err, out) {
          $('#converting-jobs').html(out);
          // order by addTime desc
          $('#table-convert-stat').dataTable({ 'order': [[5, 'desc']],
            'columns': [null, null, null, null, null, null, null, null,
            null, null, null, null, null,
            { 'searchable': false, 'render': func_size_render },
            null, null, null, null, null,
            { 'searchable': false, 'render': func_size_render },
            null
            ],
            "deferRender": true });
          $('#convert-selector-all').click(function() {
            $('.convert_selector').prop('checked', $('#convert-selector-all')[0].checked );
          });
        });
        dust.render('convert-stat-history', base.push(data), function(err, out) {
          $('#convert-jobs-history').html(out);
          // order by addTime desc
          $('#table-convert-stat-history').dataTable({
            'order': [[6, 'desc']],
            'columns': [null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null,
            { 'searchable': false, 'render': func_size_render },
            null, null, null, null, null,
            { 'searchable': false, 'render': func_size_render },
            null
            ],
            "deferRender": true
          });
          $('#history-selector-all').click(function() {
            $('.history_selector').prop('checked', $('#history-selector-all')[0].checked );
          });
        });

        $('#convert-cancel-btn').click(function () {
          convert_cancel();
        });
        $('#history-cancel-btn').click(function () {
          history_cancel();
        });
      }
  }

  function nullable_date_tostring(v){
    if(v == 0)
      return '-';
    return moment(Number(v)).format('ddd MMM DD HH:mm:ss ZZ YYYY');
  }
  function func_size_render(data, type, row, meta) {
    if(type == 'display') {
      return dust.filters.fmt_bytes(data);
    }
    else return data;
  }

  function load_page() {
    load_convert_stat();
  }
  load_page();

  $(window).bind('hashchange', function () {
    load_page();
  });

  function reload_page() {
    window.location.reload();
  }

  function convert_cancel() {
    var selected = $("input:checked.convert_selector");
    var selected_ids = new Array();
    selected.each(function (index) {
      var id = $(this).closest('tr').attr('data-id');
      selected_ids.push(id);
    })
    var str_selected_ids = '';
    for (var i = 0; i < selected_ids.length; i++) {
      if (str_selected_ids.length > 0) {
        str_selected_ids += ',';
      }
      str_selected_ids += selected_ids[i];
    }
    var url = 'convertStatus?jobIds=' + str_selected_ids;
    $.ajax({ url: url, type: 'POST' }
    ).done(function (data) {
      alert('success');
      reload_page();
    }).fail(function (data) {
      alert('failed');
    }
    );
  }
  function history_cancel() {
    var selected = $("input:checked.history_selector");
    var selected_ids = new Array();
    selected.each(function (index) {
      var id = $(this).closest('tr').attr('data-id');
      selected_ids.push(id);
    })
    var str_selected_ids = '';
    for (var i = 0; i < selected_ids.length; i++) {
      if (str_selected_ids.length > 0) {
        str_selected_ids += ',';
      }
      str_selected_ids += selected_ids[i];
    }
    var url = 'historyConvertStatus?jobIds=' + str_selected_ids;
    $.ajax({ url: url, type: 'POST' }
    ).done(function (data) {
      alert('success');
    }).fail(function (data) {
      alert('failed');
    }
    );
  }
})();
