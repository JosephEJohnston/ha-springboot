@import com.noob.haspringboot.model.Stock
@import java.util.List

@param List<Stock> stocks
@param String updateTime

<!DOCTYPE html>
<html>
<head>
    <title>JTE Market</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://unpkg.com/htmx.org@2.0.0"></script>
</head>
<body class="bg-gray-100 p-8">
<div class="max-w-2xl mx-auto bg-white rounded-2xl shadow-xl">

    @template.tag.stockTable(stocks = stocks, updateTime = updateTime)

    <button hx-get="/market"
            hx-target="#market-data-container"
            hx-swap="outerHTML"
            class="m-6 bg-blue-600 text-white px-4 py-2 rounded">
        手动刷新
    </button>
</div>
</body>
</html>