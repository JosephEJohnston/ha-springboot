@import com.noob.haspringboot.model.Stock
@import java.util.List

@param List<Stock> stocks
@param String updateTime

<div id="market-data-container"
     class="p-6">
    <div class="flex justify-between items-center mb-6 text-xs text-gray-400">
        <span>最后更新: <strong class="text-gray-700 font-mono font-bold" th:text="${updateTime}">${updateTime}</strong></span>
    </div>

    <div class="space-y-3">
        @for(var stock : stocks)
            <div class="flex justify-between items-center p-4 border rounded-xl">
                <div class="flex flex-col">
                    <span class="font-bold text-gray-800">${stock.name()}</span>
                    <span class="text-xs text-gray-400">${stock.code()}</span>
                </div>
                <div class="text-xl font-bold font-mono ${stock.isUp() ? "text-red-500" : "text-green-600"}">
                    ${stock.price()}
                </div>
            </div>
        @endfor
    </div>
</div>
