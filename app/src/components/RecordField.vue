<template>
    <div class="container record-field">
        <div class="card">
            <div class="card-body">
                <!-- 要填充的内容都放在slot里面 -->
                <slot>
                </slot>
            </div>
        </div>
    </div>
</template>

<script>

</script>


<style scoped>
    div.record-field{
        margin-top: 40px;
        width: 1400px;
    }
</style>