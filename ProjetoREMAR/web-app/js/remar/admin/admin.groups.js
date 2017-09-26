/**
 * Created by garciaph on 11/09/17.
 */

$(document).ready(function () {
    var $table        = $('#groups-table');
    var $tableButtons = $('.toggleable');

    $tableButtons.domDisable();

    $table.pageMe({
        pagerSelector  : '#groups-table-pager',
        activeColor    : '#5d4037',
        showPrevNext   : true,
        hidePageNumbers: false,
        perPage        : 5
    });

    $('#search-group').keyup(function () {
        var $el   = $(this);
        var value = $el.val().toLowerCase();

        $table.children('tr').each(function () {
            var $this    = $(this);
            var name     = $this.children('.group-name').first().text();

            var nameMatches     = (name.toLowerCase().indexOf(value) !== -1);

            if (!nameMatches) $this.addClass('hidden');
            else $this.removeClass('hidden');
        });

        $table.reloadMe();
    });

    $('.warning-box .btn-flat:nth-child(2)').click(function () {
        $('.warning-box').slideUp(500);
    });

    $('#select-all-checkbox').change(function () {
        var checked = this.checked;

        $('#groups-table td input[type="checkbox"]').each(function () {
            $(this).prop('checked', checked);
        });

        if (checked) $tableButtons.domEnable();
        else $tableButtons.domDisable();
    });

    $('#groups-table td input[type="checkbox"]').change(function () {
        var checkedCB   = $('#groups-table input:checkbox:checked').length;
        var totalCB     = $('#groups-table input:checkbox').length;
        var uncheckedCB = totalCB - checkedCB;

        if (!this.checked) {
            // if one box is unchecked after the 'select all checkbox' was checked, uncheck it too
            $('#select-all-checkbox').prop('checked', false);

            if (checkedCB == 0) $tableButtons.domDisable();
        } else {
            // if this is the last unchecked box, check the 'select all checkbox'
            if (uncheckedCB == 0) $('#select-all-checkbox').prop('checked', true);

            if ($tableButtons.attr('disabled') == 'disabled') $tableButtons.domEnable();
        }
    });
});

$.fn.domDisable = function () {
    this.attr('disabled', 'disabled');
};

$.fn.domEnable = function () {
    this.removeAttr('disabled');
};
