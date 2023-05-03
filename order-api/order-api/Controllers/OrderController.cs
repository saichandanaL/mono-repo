using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;

// For more information on enabling MVC for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace order_api.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class OrderController : ControllerBase
    {
        private static readonly string[] Orders = new[]
    {
        "Order1", "Order2", "Order2"
    };

        private readonly ILogger<OrderController> _logger;

        public OrderController(ILogger<OrderController> logger)
        {
            _logger = logger;
        }

        [HttpGet(Name = "GetWeatherForecast")]
        public IEnumerable<Orders> Get()
        {
            return Enumerable.Range(1, 5).Select(index => new Orders
            {
                Date = DateOnly.FromDateTime(DateTime.Now.AddDays(index)),
                Order = Orders[Random.Shared.Next(Orders.Length)]
            })
            .ToArray();
        }
    }
}

